package com.iidr_event_processor.processor.scenarios;

import com.iidr_event_processor.annotations.KafkaListerner;

import java.util.List;

public class ControllerWithMethodWithUnknownGenericType {
    @KafkaListerner(id= KafkaListenerParameterMock.ID, topics = KafkaListenerParameterMock.TOPIC)
    public void method(List<?> list){}
}