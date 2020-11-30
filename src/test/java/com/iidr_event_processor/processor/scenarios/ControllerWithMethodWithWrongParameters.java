package com.iidr_event_processor.processor.scenarios;

import com.iidr_event_processor.annotations.KafkaListerner;

public class ControllerWithMethodWithWrongParameters {
    @KafkaListerner(id= KafkaListenerParameterMock.ID, topics = KafkaListenerParameterMock.TOPIC)
    public void method(Object o){}
}
