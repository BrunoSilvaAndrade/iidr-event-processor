package com.iidr_event_processor.processor.scenarios;

import com.iidr_event_processor.annotations.KafkaListerner;

import java.util.List;

public class ControllerToTestEntityWithoutFormatTimeFields {
    @KafkaListerner(id= KafkaListenerParameterMock.ID, topics = KafkaListenerParameterMock.TOPIC)
    public void listener(List<EntityWithTimeFieldsWithoutFormatAnnotation> entityWithTimeFieldsWithoutFormatAnnotationList){}
}
