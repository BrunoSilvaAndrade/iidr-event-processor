package com.iidr_event_processor.application;

import org.apache.kafka.clients.consumer.ConsumerRecord;

@FunctionalInterface
public interface IIdrEntityParserErrorCallback {
  public void call(Exception e, ConsumerRecord consumerRecord) throws Exception;
}
