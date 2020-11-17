package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.annotations.*;
import br.com.viavarejo.iidr_event_processor.exceptions.EntityWrongImplementationException;
import br.com.viavarejo.iidr_event_processor.exceptions.ListenerWrongImplemetationException;
import br.com.viavarejo.iidr_event_processor.exceptions.UnsupportedTypeException;

import java.lang.reflect.*;
import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;

public class ListenersProcessorFactory {

  public static List<Listener> getListeners(final Object listenerControllerObject) throws ClassNotFoundException, EntityWrongImplementationException, ListenerWrongImplemetationException {
    final Class<?>  listenerControllerClass = listenerControllerObject.getClass();
    final Set<Method> listeners = getListenersFromControllerClass(listenerControllerClass);
    return getListenerList(listeners);
  }

  private static Set<Method> getListenersFromControllerClass(Class<?> listenerControllerClass) {
    final Set<Method> listeners = new HashSet<>();
    for (Method method : listenerControllerClass.getDeclaredMethods()) {
      method.setAccessible(true);
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
      final List<Processor> processorList = mountProcessorList(entityClass);
      listenerList.add(new Listener(method, new EntityProcessor(entityClass, processorList)));
    }
    return listenerList;
  }

  private static List<Processor> mountProcessorList(Class<?> entityClass) throws EntityWrongImplementationException  {
    final List<Processor> processorList = new ArrayList<>();
    for (Field declaredField : getAllEntityFieldsHierarchy(entityClass)) {
        if(isNotIgnoredField(declaredField)){
          declaredField.setAccessible(true);
          processorList.add(mountFieldProcessor(declaredField));
        }
    }

    for (Method declaredMethod : getAllEntityMethodsHierarchy(entityClass)) {
      if(declaredMethod.isAnnotationPresent(IIDRSetter.class)){
        declaredMethod.setAccessible(true);
        processorList.add(mountMethodProcessor(declaredMethod));
      }
    }

    return Collections.unmodifiableList(processorList);
  }

  private static List<Method> getAllEntityMethodsHierarchy(Class<?> entityClass) {
    final List<Method> currentClassMethods = new ArrayList<>(asList(entityClass.getDeclaredMethods()));
    final Class<?> parentClass = entityClass.getSuperclass();
    if(nonNull(parentClass))
      currentClassMethods.addAll(getAllEntityMethodsHierarchy(parentClass));
    return currentClassMethods;
  }

  private static List<Field> getAllEntityFieldsHierarchy(Class<?> entityClass) {
    final List<Field> currentClassFields = new ArrayList<>(asList(entityClass.getDeclaredFields()));
    final Class<?> parentClass = entityClass.getSuperclass();
    if(nonNull(parentClass))
      currentClassFields.addAll(getAllEntityFieldsHierarchy(parentClass));
    return currentClassFields;
  }

  private static boolean isNotIgnoredField(Field declaredField) {
    return !declaredField.isAnnotationPresent(IIDRIgnore.class);
  }

  private static Set<String> getAllFieldPossibleNames(Field declaredField) {
    final Set<String> fieldNames = new HashSet<>();
    fieldNames.add(declaredField.getName());
    final IIDRAlias iidrAlias = declaredField.getAnnotation(IIDRAlias.class);
    if(nonNull(iidrAlias))
      fieldNames.addAll(asList(iidrAlias.value()));
    return Collections.unmodifiableSet(fieldNames);
  }

  private static Processor mountMethodProcessor(Method method) throws EntityWrongImplementationException {
    final Set<String> fieldNames = new HashSet<>(asList(method.getAnnotation(IIDRSetter.class).value()));
    if(method.getParameterCount() == 1) {
      final Parameter parameter = method.getParameters()[0];
      final boolean mayBeNull = !parameter.isAnnotationPresent(IIDRNonNull.class);
      try {
        return new MethodProcessor(method, FieldParseFactory.getParserByType(parameter.getType(), parameter.getAnnotation(IIDRPattern.class)), mayBeNull, fieldNames);
      } catch (UnsupportedTypeException unsupportedTypeException) {
        throw new EntityWrongImplementationException("A method IIDRSetter annotated only must have a type IIDR's parsable, not custom entities");
      }
    }
    throw new EntityWrongImplementationException(format("A method IIDRSetter annotated must expect only one argument, the method <%s> is not expecting it", method.getName()));
  }


  private static Processor mountFieldProcessor(Field field) throws EntityWrongImplementationException {
    final boolean mayBeNull = !field.isAnnotationPresent(IIDRNonNull.class);
    final Set<String> fieldNames = getAllFieldPossibleNames(field);
    try {
      return new FieldProcessor(field, FieldParseFactory.getParserByType(field.getType(), field.getAnnotation(IIDRPattern.class)), mayBeNull, fieldNames);
    } catch (UnsupportedTypeException ignored) {
      final EntityProcessor entityProcessor = new EntityProcessor(field.getType(), mountProcessorList(field.getType()));
      return  new FieldProcessor(field, entityProcessor);
    }
  }
}