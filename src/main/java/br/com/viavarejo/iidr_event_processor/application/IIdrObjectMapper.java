package br.com.viavarejo.iidr_event_processor.application;

import br.com.viavarejo.iidr_event_processor.exceptions.FieldMayBeNotNullException;
import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;
import br.com.viavarejo.iidr_event_processor.processor.EntityProcessor;
import br.com.viavarejo.iidr_event_processor.processor.Processor;
import org.json.simple.JSONObject;

import java.util.Set;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class IIdrObjectMapper {

  public static Object mapObject(EntityProcessor entityProcessor, JSONObject jsonObject) throws IIdrApplicationException, FieldMayBeNotNullException {
    final Object entityObject = map(entityProcessor, jsonObject);
    if(!jsonObject.isEmpty() && nonNull(entityProcessor.nonMappedFieldProcessor)) {
      entityProcessor.nonMappedFieldProcessor.process(entityObject, jsonObject);
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
      if(isNull(iidrValue) && !processor.mayBeNull) {
        throw new FieldMayBeNotNullException(format("No one of these <%s> was found in IIDR value ", processor.fieldNames.toString()));
      }else if(nonNull(iidrValue)){
        processor.process(entityObject, iidrValue.trim());
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
