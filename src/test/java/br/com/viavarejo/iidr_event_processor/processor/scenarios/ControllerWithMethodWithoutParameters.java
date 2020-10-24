package br.com.viavarejo.iidr_event_processor.processor.scenarios;

import br.com.viavarejo.iidr_event_processor.annotations.KafkaListerner;

public class ControllerWithMethodWithoutParameters {
    @KafkaListerner(id = KafkaListenerParameterMock.ID, topics = KafkaListenerParameterMock.TOPIC)
    public void method(){}
}
