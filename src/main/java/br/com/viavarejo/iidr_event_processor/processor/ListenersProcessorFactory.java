package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.annotations.*;
import br.com.viavarejo.iidr_event_processor.exceptions.EntityWrongImplementationException;
import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;
import br.com.viavarejo.iidr_event_processor.exceptions.ListenerWrongImplemetationException;
import br.com.viavarejo.iidr_event_processor.exceptions.UnsupportedTypeException;

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

public class ListenersProcessorFactory {
  private static final FieldParserAndSetter STRING = (o,f,v) -> f.set(o, v);
  private static final FieldParserAndSetter LONG = (o,f,v) -> f.set(o, Long.parseLong(v));
  private static final FieldParserAndSetter INT = (o,f,v) -> f.set(o, Integer.parseInt(v));
  private static final FieldParserAndSetter FLOAT = (o,f,v) -> f.set(o, Float.parseFloat(v));
  private static final FieldParserAndSetter DOUBLE = (o,f,v) -> f.set(o, Double.parseDouble(v));
  private static final FieldParserAndSetter BOOLEAN = (o,f,v) -> f.set(o, Boolean.parseBoolean(v));

  public static List<Listener> getListeners(final Object listenerControllerObject) throws ClassNotFoundException, EntityWrongImplementationException, ListenerWrongImplemetationException {
    final Class<?>  listenerControllerClass = listenerControllerObject.getClass();
    final Set<Method> listeners = getListenersFromControllerClass(listenerControllerClass);
    return getListenerList(listeners);
  }

  private static Set<Method> getListenersFromControllerClass(Class<?> listenerControllerClass) {
    final Set<Method> listeners = new HashSet<>();
    for (Method method : listenerControllerClass.getMethods()) {
      if(method.isAnnotationPresent(KafkaListerner.class))
        listeners.add(method);
    }
    return listeners;
  }

  private static List<Listener> getListenerList(Set<Method> methodSet) throws  EntityWrongImplementationException, ListenerWrongImplemetationException, ClassNotFoundException {
    final List<Listener> listenerList = new ArrayList<>();

    for (Method method : methodSet) {
      final Type[] genericParameterTypes = method.getGenericParameterTypes();
      final Parameter[] parameters = method.getParameters();

      if (parameters.length != 1 || !parameters[0].getType().equals(List.class))
        throw new ListenerWrongImplemetationException(format("The listeners must receive only one List of some entity, the method <%s> is not expecting it", method.getName()));

      final Type type = ((ParameterizedType)genericParameterTypes[0]).getActualTypeArguments()[0];

      Class<?> entityClass = Class.forName(type.getTypeName());
      final List<FieldProcessor> fieldProcessorList = mountFieldProcessorList(entityClass);
      listenerList.add(new Listener(method, new EntityProcessor(entityClass, fieldProcessorList)));
    }
    return listenerList;
  }

  private static List<FieldProcessor> mountFieldProcessorList(Class<?> entityClass) throws EntityWrongImplementationException  {
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
    return Collections.unmodifiableSet(fieldNames);
  }

  private static FieldProcessor mountFieldProcessor(Field field) throws EntityWrongImplementationException {
    final boolean mayBeNull = !field.isAnnotationPresent(NonNull.class);
    final Set<String> fieldNames = getAllFieldPossibleNames(field);
    try {
      final FieldParserAndSetter fieldParserAndSetter = getFieldParserByType(field);
      return new FieldProcessor(field, fieldParserAndSetter, mayBeNull, fieldNames);
    } catch (UnsupportedTypeException ignored) {
      final EntityProcessor entityProcessor = new EntityProcessor(field.getType(), mountFieldProcessorList(field.getType()));
      return  new FieldProcessor(field, entityProcessor);
    }
  }

  private static String getPattern(Field field) throws EntityWrongImplementationException {
    Pattern pattern = field.getAnnotation(Pattern.class);
    if(isNull(pattern))
      throw new EntityWrongImplementationException(format("Annotation Pattern is needed on field <%s>", field.getName()));
    return pattern.value();
  }

  private static FieldParserAndSetter getFieldParserByType(Field field) throws UnsupportedTypeException, EntityWrongImplementationException {
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
      final String pattern = getPattern(field);
      return (o,f,v) -> f.set(o, Date.from(LocalDate.parse(v, DateTimeFormatter.ofPattern(pattern)).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    if(java.sql.Date.class.equals(type)){
      final String pattern = getPattern(field);
      return (o,f,v)  -> f.set(o, java.sql.Date.valueOf(LocalDate.parse(v, DateTimeFormatter.ofPattern(pattern))));
    }

    if(Time.class.equals(type)) {
      final String pattern = getPattern(field);
      return (o,f,v) -> f.set(o, Time.valueOf(LocalTime.parse(v, DateTimeFormatter.ofPattern(pattern))));
    }

    if(Timestamp.class.equals(type)) {
      final String pattern = getPattern(field);
      return (o,f,v) -> f.set(o, Timestamp.valueOf(LocalDateTime.parse(v, DateTimeFormatter.ofPattern(pattern))));
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

    throw new UnsupportedTypeException(format("IIdrApplication does not support type <%s> of field <%s>", type.getCanonicalName(), field.getName()));
  }
}