package br.com.viavarejo.iidr_event_processor.processor;

import java.lang.reflect.Method;

public class Listener {
     public final Method method;
     public final EntityProcessor entityProcessor;
     Listener(Method method, EntityProcessor entityProcessor) {
         this.method = method;
         this.entityProcessor = entityProcessor;
     }
}
