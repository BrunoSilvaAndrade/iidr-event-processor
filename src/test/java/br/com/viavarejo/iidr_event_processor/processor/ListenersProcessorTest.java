package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.exceptions.EntityWrongImplementationException;
import br.com.viavarejo.iidr_event_processor.exceptions.ListenerWrongImplemetationException;
import br.com.viavarejo.iidr_event_processor.exceptions.UnsuporttedTypeException;
import br.com.viavarejo.iidr_event_processor.mock.*;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;
import java.util.List;

public class ListenersProcessorTest {
    @Test
    public void testControllerWithMethodWithoutKafkaAnnotation() throws ClassNotFoundException, EntityWrongImplementationException, UnsuporttedTypeException, ListenerWrongImplemetationException {
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
    public  void testEntityWithUnsupportedTypes() {
        assertThrows(UnsuporttedTypeException.class, () -> ListenersProcessor.getListeners(new ControllerToTesteEntityWithUnsupportedTypes()));
    }

}
