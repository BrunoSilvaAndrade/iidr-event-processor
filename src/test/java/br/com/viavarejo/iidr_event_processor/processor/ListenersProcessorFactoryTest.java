package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.exceptions.EntityWrongImplementationException;
import br.com.viavarejo.iidr_event_processor.exceptions.ListenerWrongImplemetationException;
import br.com.viavarejo.iidr_event_processor.processor.scenarios.*;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class ListenersProcessorFactoryTest {
    @Test
    public void testControllerWithMethodWithoutKafkaAnnotation() throws ClassNotFoundException, EntityWrongImplementationException, ListenerWrongImplemetationException {
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
}
