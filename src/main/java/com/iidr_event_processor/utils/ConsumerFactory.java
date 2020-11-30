package com.iidr_event_processor.utils;

import com.iidr_event_processor.annotations.KafkaListerner;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

import static java.util.Arrays.asList;

public abstract class ConsumerFactory {
  public static Consumer<String, String> getConsumer(KafkaListerner kafkaListerner, Properties consumerProps) {
    final Properties props = new Properties();
    props.putAll(consumerProps);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaListerner.id());
    final Consumer<String, String> consumer = new KafkaConsumer<>(props, new StringDeserializer(), new StringDeserializer());
    consumer.subscribe(asList(kafkaListerner.topics()));
    return consumer;
  }
}
