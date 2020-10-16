package br.com.viavarejo.iidr_event_processor.processor;

import java.lang.reflect.Field;
import java.util.Set;

public class FieldProcessor {
  final public Field field;
  final public FieldParserAndSetter fieldParserAndSetter;
  final public boolean mayBeNull;
  final public Set<String> fieldNames;

  FieldProcessor(Field field, FieldParserAndSetter fieldParserAndSetter, boolean mayBeNull, Set<String> fieldNames) {
    this.field = field;
    this.fieldParserAndSetter = fieldParserAndSetter;
    this.mayBeNull = mayBeNull;
    this.fieldNames = fieldNames;
  }

  public void proccessField(Object object, String iidrValue) throws Exception {
    this.fieldParserAndSetter.parseAndSet(object, field, iidrValue);
  }

  public String getNativeFieldName() {
    return field.getName();
  }
}
