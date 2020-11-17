package br.com.viavarejo.iidr_event_processor.processor;

@FunctionalInterface
public interface FieldParser {
  Object parse(String iidrValue) throws Exception;
}
