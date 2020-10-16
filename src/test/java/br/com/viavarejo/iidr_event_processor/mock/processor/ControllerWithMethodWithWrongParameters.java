package br.com.viavarejo.iidr_event_processor.mock.processor;

import br.com.viavarejo.iidr_event_processor.annotations.KafkaListerner;
import static br.com.viavarejo.iidr_event_processor.mock.processor.KafkaListenerParameterMock.ID;
import static br.com.viavarejo.iidr_event_processor.mock.processor.KafkaListenerParameterMock.TOPIC;
public class ControllerWithMethodWithWrongParameters {
    @KafkaListerner(id=ID, topics = TOPIC)
    public void method(Object o){}
}
