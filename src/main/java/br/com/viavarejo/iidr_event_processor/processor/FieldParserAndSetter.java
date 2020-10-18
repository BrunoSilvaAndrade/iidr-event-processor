package br.com.viavarejo.iidr_event_processor.processor;

import java.lang.reflect.Field;

@FunctionalInterface
public interface FieldParserAndSetter {
  void parseAndSet(Object object, Field field, String iidrValue) throws Exception;
}
