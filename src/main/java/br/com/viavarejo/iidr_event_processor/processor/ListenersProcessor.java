package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.annotations.*;
import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;

import java.lang.reflect.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ListenersProcessor {
  private static final FieldParserAndSetter STRING = (o,f,v) -> f.set(o, v);
  private static final FieldParserAndSetter LONG = (o,f,v) -> f.set(o, Long.parseLong(v));
  private static final FieldParserAndSetter INT = (o,f,v) -> f.set(o, Integer.parseInt(v));
  private static final FieldParserAndSetter FLOAT = (o,f,v) -> f.set(o, Float.parseFloat(v));
  private static final FieldParserAndSetter DOUBLE = (o,f,v) -> f.set(o, Double.parseDouble(v));
  private static final FieldParserAndSetter BOOLEAN = (o,f,v) -> f.set(o, Boolean.parseBoolean(v));

  public static List<Listener> getListeners(final Object listenerControllerObject) throws IIdrApplicationException {
    final Class<?>  listenerControllerClass = listenerControllerObject.getClass();
    final Set<Method> listeners = getListenersFromControllerClass(listenerControllerClass);
    return getListenerList(listeners);
  }

  public static Set<Method> getListenersFromControllerClass(Class<?> listenerControllerClass) {
    final Set<Method> listeners = new HashSet<>();
    for (Method method : listenerControllerClass.getMethods()) {
      if(method.isAnnotationPresent(KafkaListerner.class))
        listeners.add(method);
    }
    return listeners;
  }

  private static List<Listener> getListenerList(Set<Method> methodSet) throws IIdrApplicationException {
    final List<Listener> listenerList = new ArrayList<>();

    for (Method method : methodSet) {
      final Type[] genericParameterTypes = method.getGenericParameterTypes();
      final Parameter[] parameters = method.getParameters();

      if (parameters.length != 1 || !parameters[0].getType().equals(List.class))
        throw new IIdrApplicationException(format("The listeners must receive only one List of some entity, the method <%s> is not expecting it", method.getName()));

      final Type type = ((ParameterizedType)genericParameterTypes[0]).getActualTypeArguments()[0];

      try {
        Class<?> entityClass = Class.forName(type.getTypeName());
        final List<FieldProcessor> fieldProcessorList = mountFieldProcessorMap(entityClass);
        listenerList.add(new Listener(method, new EntityProcessor(entityClass, fieldProcessorList)));
      } catch (ClassNotFoundException cnfe){
        throw new IIdrApplicationException(format("Entity class <%s> not found", type.getTypeName()));
      }
    }
    return listenerList;
  }

  private static List<FieldProcessor> mountFieldProcessorMap(Class<?> entityClass) throws IIdrApplicationException {
    final List<FieldProcessor> fieldProcessorList = new ArrayList<>();
    for (Field declaredField : getAllEntityFieldsHierarchy(entityClass)) {
        if(isNotIgnoredField(declaredField)){
          declaredField.setAccessible(true);
          fieldProcessorList.add(mountFieldProcessor(declaredField));
        }
    }
    return fieldProcessorList;
  }

  private static List<Field> getAllEntityFieldsHierarchy(Class<?> entityClass) {
    final List<Field> currentClassFields = new ArrayList<>(asList(entityClass.getDeclaredFields()));
    final Class<?> parentClass = entityClass.getSuperclass();
    if(nonNull(parentClass))
      currentClassFields.addAll(getAllEntityFieldsHierarchy(parentClass));
    return currentClassFields;
  }

  private static boolean isNotIgnoredField(Field declaredField) {
    return !declaredField.isAnnotationPresent(Ignore.class);
  }

  private static Set<String> getAllFieldPossibleNames(Field declaredField) {
    final Set<String> fieldNames = new HashSet<>();
    fieldNames.add(declaredField.getName());
    final Alias alias = declaredField.getAnnotation(Alias.class);
    if(nonNull(alias))
      fieldNames.addAll(asList(alias.value()));
    return fieldNames;
  }

  private static FieldProcessor mountFieldProcessor(Field field) throws IIdrApplicationException {
    final FieldParserAndSetter fieldParserAndSetter = getFieldParserByType(field);
    final boolean mayBeNull = !field.isAnnotationPresent(NonNull.class);
    final Set<String> fieldNames = getAllFieldPossibleNames(field);
    return new FieldProcessor(field, fieldParserAndSetter, mayBeNull, fieldNames);
  }

  private static String getFormat(Field field) throws IIdrApplicationException {
    Format format = field.getAnnotation(Format.class);
    if(isNull(format))
      throw new IIdrApplicationException(format("Annotation Format is needed on field <%s>", field.getName()));
    return format.value();
  }

  private static FieldParserAndSetter getFieldParserByType(Field field) throws IIdrApplicationException {
    final Class<?> type = field.getType();

    if(String.class.equals(type)) {
      return STRING;
    }

    if (long.class.equals(type) || Long.class.equals(type)) {
      return LONG;
    }

    if(int.class.equals(type) || Integer.class.equals(type)) {
      return INT;
    }

    if(float.class.equals(type) || Float.class.equals(type)) {
      return FLOAT;
    }

    if(double.class.equals(type) || Double.class.equals(type)) {
      return DOUBLE;
    }

    if(boolean.class.equals(type) || Boolean.class.equals(type)) {
      return BOOLEAN;
    }

    if(Date.class.equals(type)) {
      final String format = getFormat(field);
      return (o,f,v) -> f.set(o, Date.from(LocalDate.parse(v, DateTimeFormatter.ofPattern(format)).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    if(java.sql.Date.class.equals(type)){
      final String format = getFormat(field);
      return (o,f,v)  -> f.set(o, java.sql.Date.valueOf(LocalDate.parse(v, DateTimeFormatter.ofPattern(format))));
    }

    if(Time.class.equals(type)) {
      final String format = getFormat(field);
      return (o,f,v) -> f.set(o, Time.valueOf(LocalTime.parse(v, DateTimeFormatter.ofPattern(format))));
    }

    if(Timestamp.class.equals(type)) {
      final String format = getFormat(field);
      return (o,f,v) -> f.set(o, Timestamp.valueOf(LocalDateTime.parse(v, DateTimeFormatter.ofPattern(format))));
    }

    if(type.isEnum()) {
      return (o,f,v) -> {
        for (Object enumField : type.getEnumConstants()) {
          if(((Enum<?>)enumField).name().equals(v)) {
            f.set(o, enumField);
            return;
          }
        }
        throw new IIdrApplicationException(format("Value <%s> is not present in enum <%s>", v, type.getCanonicalName()));
      };
    }

    throw new IIdrApplicationException(format("IIdrApplication does not support type <%s>", type.getCanonicalName()));
  }
}