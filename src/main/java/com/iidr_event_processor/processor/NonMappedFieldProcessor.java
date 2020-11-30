package com.iidr_event_processor.processor;

import com.iidr_event_processor.exceptions.IIdrApplicationException;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class NonMappedFieldProcessor {
  final Field field;

  public NonMappedFieldProcessor(Field field){
    this.field = field;
  }

  public void process(Object entityObject, JSONObject jsonObject) throws IIdrApplicationException {
    final Map<String, String> nonMappedFields = new HashMap<>();
    jsonObject.forEach((key,value) -> nonMappedFields.put((String)key, (String) value));
    try {
      field.set(entityObject, nonMappedFields);
    } catch (IllegalAccessException e) {
      throw new IIdrApplicationException(format("Fail to set nonMappedFields on Field <%s> of class <%s>", field.getName(), entityObject.getClass().getCanonicalName()), e);
    }
  }
}
