package com.iidr_event_processor.processor;

import com.iidr_event_processor.annotations.KafkaListerner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class Listener {
     private final Object listenersControllerObject;
     private final Method method;
     public final EntityProcessor entityProcessor;

     Listener(Object  listenersControllerObject, Method method, EntityProcessor entityProcessor) {
         this.listenersControllerObject = listenersControllerObject;
         this.method = method;
         this.entityProcessor = entityProcessor;
     }

     public void injectEntityObjectList(List<Object> entityObjectList) throws InvocationTargetException, IllegalAccessException {
        method.invoke(listenersControllerObject, entityObjectList);
     }

     public KafkaListerner getKafkaListenerAnnotation(){
       return method.getDeclaredAnnotation(KafkaListerner.class);
     }
}
