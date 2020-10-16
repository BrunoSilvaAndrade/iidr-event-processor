package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.exceptions.EntityWrongImplementationException;
import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;
import br.com.viavarejo.iidr_event_processor.exceptions.ListenerWrongImplemetationException;
import br.com.viavarejo.iidr_event_processor.exceptions.UnsuporttedTypeException;
import br.com.viavarejo.iidr_event_processor.mock.RightControllerImplementation;
import br.com.viavarejo.iidr_event_processor.mock.RightEntityImplementation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class EntityProcessorTest {
    List<Listener> listenerList;

    @Before
    public void init() throws ClassNotFoundException, EntityWrongImplementationException, UnsuporttedTypeException, ListenerWrongImplemetationException {
        listenerList = ListenersProcessor.getListeners(new RightControllerImplementation());
    }

    @Test
    public void testIfFieldProcessorListIsNotEmpty(){
        listenerList.forEach(listener -> assertFalse(listener.entityProcessor.getFieldProcessorList().isEmpty()));
    }

    @Test
    public void testIfTheEntityClassIsRight(){
        listenerList.forEach(listener -> {
            try {
                assertEquals(RightEntityImplementation.class,listener.entityProcessor.getEntityClassInstance().getClass());
            } catch (IIdrApplicationException e) {
                fail("Any exception may be not thrown");
            }
        });
    }}
