package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;

import java.lang.reflect.Field;
import java.util.Set;

import static java.lang.String.format;

public class FieldProcessor extends Processor {
  final public Field field;

  FieldProcessor(Field field, FieldParser fieldParser, boolean mayBeNull, Set<String> fieldNames, boolean isCustomEntity, EntityProcessor entityProcessor) {
    super(fieldParser, mayBeNull, fieldNames, isCustomEntity, entityProcessor);
    this.field = field;
  }

  FieldProcessor(Field field, FieldParser fieldParser, boolean mayBeNull, Set<String> fieldNames) {
    this(field, fieldParser, mayBeNull, fieldNames, false, null);
  }

  FieldProcessor(Field field, EntityProcessor entityProcessor){
    this(field, null, false, null, true, entityProcessor);
  }

  public void processCustomField(Object entityObject, Object customEntityObject) throws IIdrApplicationException {
    try {
      field.set(entityObject, customEntityObject);
    } catch (IllegalAccessException ie) {
      throw new IIdrApplicationException(ie);
    }

  }

  @Override
  public void process(Object entityObject, String iidrValue) throws IIdrApplicationException {
    try{
      field.set(entityObject, fieldParser.parse(iidrValue));
    }catch (Exception e){
      throw new IIdrApplicationException(format("Fail to parse iidrValue <%s> of field <%s> of class %s", iidrValue, getNativeFieldName(), entityObject.getClass().getCanonicalName()), e);
    }
  }

  public String getNativeFieldName() {
    return field.getName();
  }
}
