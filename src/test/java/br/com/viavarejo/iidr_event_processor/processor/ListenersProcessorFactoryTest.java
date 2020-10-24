package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.exceptions.EntityWrongImplementationException;
import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;
import br.com.viavarejo.iidr_event_processor.exceptions.ListenerWrongImplemetationException;
import br.com.viavarejo.iidr_event_processor.exceptions.UnsupportedTypeException;
import br.com.viavarejo.iidr_event_processor.processor.scenarios.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ListenersProcessorFactoryTest {
    @Test
    public void testControllerWithMethodWithoutKafkaAnnotation() throws ClassNotFoundException, EntityWrongImplementationException, UnsupportedTypeException, ListenerWrongImplemetationException {
        List<Listener> listenerList = ListenersProcessorFactory.getListeners(new ControllerWithMethodWithoutKafkaListenerAnnotation());
        assertTrue(listenerList.isEmpty());
    }

    @Test
    public void testControllerWithMethodWithoutParameters(){
        assertThrows(ListenerWrongImplemetationException.class, () -> ListenersProcessorFactory.getListeners(new ControllerWithMethodWithoutParameters()));
    }

    @Test
    public void testControllerWithMethodWithWrongParameters(){
        assertThrows(ListenerWrongImplemetationException.class, () -> ListenersProcessorFactory.getListeners(new ControllerWithMethodWithWrongParameters()));
    }

    @Test
    public void testControllerWithMethodWithUnknownGenericType(){
        assertThrows(ClassNotFoundException.class, () -> ListenersProcessorFactory.getListeners(new ControllerWithMethodWithUnknownGenericType()));
    }

    @Test
    public void testEntityWithTimFieldsWithoutFormatAnnotatiion(){
        assertThrows(EntityWrongImplementationException.class, () -> ListenersProcessorFactory.getListeners(new ControllerToTestEntityWithoutFormatTimeFields()));
    }

    @Test
    public  void testEntityWithUnsupportedTypes() throws ClassNotFoundException, EntityWrongImplementationException, ListenerWrongImplemetationException, UnsupportedTypeException, IIdrApplicationException {
        final List<Listener> listenerList = ListenersProcessorFactory.getListeners(new ControllerToTestEntityWithCustomTypes());
        boolean isCustomEntity = false;
        Object entityObject = null;
        for (Listener listener : listenerList) {
            for (FieldProcessor fieldProcessor : listener.entityProcessor.getFieldProcessorList()) {
                isCustomEntity = fieldProcessor.isCustomEntity;
                entityObject = fieldProcessor.entityProcessor.getEntityClassInstance();
            }
        }
        assertTrue(isCustomEntity);
        assertEquals(RightEntityImplementation.class, entityObject.getClass());
    }

}
