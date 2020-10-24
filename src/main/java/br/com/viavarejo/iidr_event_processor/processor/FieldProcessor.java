package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;

import java.lang.reflect.Field;
import java.util.Set;

import static java.lang.String.format;

public class FieldProcessor {
  final public Field field;
  final public boolean mayBeNull;
  final public boolean isCustomEntity;
  final public Set<String> fieldNames;
  final public EntityProcessor entityProcessor;
  final public FieldParserAndSetter fieldParserAndSetter;

  FieldProcessor(Field field, FieldParserAndSetter fieldParserAndSetter, boolean mayBeNull, Set<String> fieldNames, boolean isCustomEntity, EntityProcessor entityProcessor) {
    this.field = field;
    this.fieldParserAndSetter = fieldParserAndSetter;
    this.mayBeNull = mayBeNull;
    this.fieldNames = fieldNames;
    this.isCustomEntity = isCustomEntity;
    this.entityProcessor = entityProcessor;
  }

  FieldProcessor(Field field, FieldParserAndSetter fieldParserAndSetter, boolean mayBeNull, Set<String> fieldNames) {
    this(field, fieldParserAndSetter, mayBeNull, fieldNames, false, null);
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

  public void proccessField(Object entityObject, String iidrValue) throws IIdrApplicationException {
    try{
      this.fieldParserAndSetter.parseAndSet(entityObject, field, iidrValue);
    }catch (Exception e){
      throw new IIdrApplicationException(format("Fail to parse iidrValue \"%s\" to set in field <%s> of class %s", iidrValue, getNativeFieldName(), entityObject.getClass().getCanonicalName()), e);
    }
  }

  public String getNativeFieldName() {
    return field.getName();
  }
}
