package com.iidr_event_processor.processor;

import com.iidr_event_processor.exceptions.FieldMayBeNotNullException;
import com.iidr_event_processor.exceptions.IIdrApplicationException;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

public class EntityProcessor {
  private final Class<?> entityClass;
  private final List<Processor> processorList;
  private final NonMappedFieldProcessor nonMappedFieldProcessor;

  public EntityProcessor(Class<?> entityClass, List<Processor> processorList, NonMappedFieldProcessor nonMappedFieldProcessor) {
    this.entityClass = entityClass;
    this.processorList = processorList;
    this.nonMappedFieldProcessor = nonMappedFieldProcessor;
  }

  public EntityProcessor(Class<?> entityClass, List<Processor> processorList) {
    this(entityClass, processorList, null);
  }

  private Object getEntityClassInstance() throws IIdrApplicationException {
    try {
      return entityClass.newInstance();
    } catch (IllegalAccessException | InstantiationException e) {
      throw new IIdrApplicationException(String.format("Fail to create a new instance of entity Class <%s> cause: %s", entityClass.getCanonicalName(), e.getMessage()));
    }
  }

  public Object bind(JSONObject jsonObject) throws IIdrApplicationException, FieldMayBeNotNullException {
    final Object entityObject = map(this, jsonObject);
    if(!jsonObject.isEmpty() && nonNull(nonMappedFieldProcessor)) {
      nonMappedFieldProcessor.process(entityObject, jsonObject);
    }
    return entityObject;
  }

  private static Object map(EntityProcessor entityProcessor, JSONObject jsonObject) throws IIdrApplicationException, FieldMayBeNotNullException {
    final Object entityObject = entityProcessor.getEntityClassInstance();
    for (Processor processor : entityProcessor.processorList) {
      if(processor.isCustomEntity){
        processor.processCustomField(entityObject, map(processor.entityProcessor, jsonObject));
        continue;
      }
      final String iidrValue = tryFindIIdrValue(processor.fieldNames, jsonObject);
      if(nonNull(iidrValue)){
        processor.process(entityObject, iidrValue.trim());
      }else if(!processor.mayBeNull){
        throw new FieldMayBeNotNullException(format("No one of these <%s> was found in IIDR value ", processor.fieldNames.toString()));
      }
    }
    return entityObject;
  }

  private static String tryFindIIdrValue(Set<String> fieldNames, JSONObject jsonObject) {
    for (String fieldName : fieldNames) {
      final String value = (String) jsonObject.remove(fieldName);
      if(nonNull(value))
        return value;
    }
    return null;
  }
}
