package com.iidr_event_processor.processor;

@FunctionalInterface
public interface IIdrValueParser {
  Object parse(String iidrValue) throws Exception;
}