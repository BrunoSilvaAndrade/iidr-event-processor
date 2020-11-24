package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;

import java.lang.reflect.Field;
import java.util.Set;

import static java.lang.String.format;

public class FieldProcessor extends Processor {
  final public Field field;

  FieldProcessor(Field field, IIdrValueParser iidrValueParser, boolean mayBeNull, Set<String> fieldNames) {
    super(iidrValueParser, mayBeNull, fieldNames, false, null);
    this.field = field;
  }

  FieldProcessor(Field field, EntityProcessor entityProcessor){
    super(null, false, null, true, entityProcessor);
    this.field = field;
  }

  @Override
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
      field.set(entityObject, iidrValueParser.parse(iidrValue));
    }catch (Exception e){
      throw new IIdrApplicationException(format("Fail to parse iidrValue <%s> of field <%s> of class %s", iidrValue, field.getName(), entityObject.getClass().getCanonicalName()), e);
    }
  }
}
