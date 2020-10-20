package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.exceptions.EntityWrongImplementationException;
import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;
import br.com.viavarejo.iidr_event_processor.exceptions.ListenerWrongImplemetationException;
import br.com.viavarejo.iidr_event_processor.exceptions.UnsupportedTypeException;
import br.com.viavarejo.iidr_event_processor.mock.processor.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ListenersProcessorTest {
    @Test
    public void testControllerWithMethodWithoutKafkaAnnotation() throws ClassNotFoundException, EntityWrongImplementationException, UnsupportedTypeException, ListenerWrongImplemetationException {
        List<Listener> listenerList = ListenersProcessor.getListeners(new ControllerWithMethodWithoutKafkaListenerAnnotation());
        assertTrue(listenerList.isEmpty());
    }

    @Test
    public void testControllerWithMethodWithoutParameters(){
        assertThrows(ListenerWrongImplemetationException.class, () -> ListenersProcessor.getListeners(new ControllerWithMethodWithoutParameters()));
    }

    @Test
    public void testControllerWithMethodWithWrongParameters(){
        assertThrows(ListenerWrongImplemetationException.class, () -> ListenersProcessor.getListeners(new ControllerWithMethodWithWrongParameters()));
    }

    @Test
    public void testControllerWithMethodWithUnknownGenericType(){
        assertThrows(ClassNotFoundException.class, () -> ListenersProcessor.getListeners(new ControllerWithMethodWithUnknownGenericType()));
    }

    @Test
    public void testEntityWithTimFieldsWithoutFormatAnnotatiion(){
        assertThrows(EntityWrongImplementationException.class, () -> ListenersProcessor.getListeners(new ControllerToTestEntityWithoutFormatTimeFields()));
    }

    @Test
    public  void testEntityWithUnsupportedTypes() throws ClassNotFoundException, EntityWrongImplementationException, ListenerWrongImplemetationException, UnsupportedTypeException, IIdrApplicationException {
        final List<Listener> listenerList = ListenersProcessor.getListeners(new ControllerToTestEntityWithCustomTypes());
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
