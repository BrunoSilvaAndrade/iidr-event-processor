package br.com.viavarejo.iidr_event_processor.application;

import java.util.Map;

@FunctionalInterface
public interface IIdrEntityParserErrorCallback {
  public void call(Exception e, Map<String, String> iIdrEventDeserialized, String iidrNativeEvent) throws Exception;
}
