package br.com.viavarejo.iidr_event_processor.mock;

import br.com.viavarejo.iidr_event_processor.annotations.KafkaListerner;
import static br.com.viavarejo.iidr_event_processor.mock.KafkaListenerParameterMock.ID;
import static br.com.viavarejo.iidr_event_processor.mock.KafkaListenerParameterMock.TOPIC;
public class ControllerWithMethodWithWrongParameters {
    @KafkaListerner(id=ID, topics = TOPIC)
    public void method(Object o){}
}
