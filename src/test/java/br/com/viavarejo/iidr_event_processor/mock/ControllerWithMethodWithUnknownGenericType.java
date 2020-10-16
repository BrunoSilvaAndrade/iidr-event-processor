package br.com.viavarejo.iidr_event_processor.mock;

import br.com.viavarejo.iidr_event_processor.annotations.KafkaListerner;

import java.util.List;

import static br.com.viavarejo.iidr_event_processor.mock.KafkaListenerParameterMock.ID;
import static br.com.viavarejo.iidr_event_processor.mock.KafkaListenerParameterMock.TOPIC;

public class ControllerWithMethodWithUnknownGenericType {
    @KafkaListerner(id=ID, topics = TOPIC)
    public void method(List<?> list){}
}