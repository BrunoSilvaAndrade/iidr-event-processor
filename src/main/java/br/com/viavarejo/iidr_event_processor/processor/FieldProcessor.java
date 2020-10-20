package br.com.viavarejo.iidr_event_processor.processor;

import java.lang.reflect.Field;
import java.util.Set;

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

  public void processCustomField(Object entityObject, Object customEntityObject) throws IllegalAccessException {
    field.set(entityObject, customEntityObject);
  }

  public void proccessField(Object object, String iidrValue) throws Exception {
    this.fieldParserAndSetter.parseAndSet(object, field, iidrValue);
  }

  public String getNativeFieldName() {
    return field.getName();
  }
}
