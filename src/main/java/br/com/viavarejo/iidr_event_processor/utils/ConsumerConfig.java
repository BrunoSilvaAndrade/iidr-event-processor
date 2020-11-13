package br.com.viavarejo.iidr_event_processor.utils;

import java.util.Map;
import java.util.Properties;

public class ConsumerConfig  extends org.apache.kafka.clients.consumer.ConsumerConfig {
    public ConsumerConfig(Properties props) {
        super(props);
    }

    public ConsumerConfig(Map<String, Object> props) {
        super(props);
    }

    protected ConsumerConfig(Map<?, ?> props, boolean doLog) {
        super(props, doLog);
    }
}
