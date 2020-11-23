package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.exceptions.EntityWrongImplementationException;
import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;
import br.com.viavarejo.iidr_event_processor.exceptions.ListenerWrongImplemetationException;
import br.com.viavarejo.iidr_event_processor.processor.scenarios.RightControllerImplementation;
import br.com.viavarejo.iidr_event_processor.processor.scenarios.RightEntityImplementation;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ProcessorTest {
    private static final String STRING_FIELD_VALUE = "valor1";
    Map<String, String> eventMapSimulation;
    List<Listener> listenerList;

    @Before
    public void init() throws ClassNotFoundException, EntityWrongImplementationException, ListenerWrongImplemetationException {
        listenerList = ListenersProcessorFactory.getListeners(new RightControllerImplementation());
        eventMapSimulation = new HashMap<>();
        eventMapSimulation.put("string1", STRING_FIELD_VALUE);
        eventMapSimulation.put("_long", "1000000");
        eventMapSimulation.put("_int", "10");
        eventMapSimulation.put("methodTest", "10");
        eventMapSimulation.put("_float", "1.0");
        eventMapSimulation.put("_double", "1.0");
        eventMapSimulation.put("_boolean", "true");
        eventMapSimulation.put("_bigDecimal", "1.0");
        eventMapSimulation.put("date", "2020-10-09");
        eventMapSimulation.put("sqlDate", "2020-10-09");
        eventMapSimulation.put("time", "20:10:09");
        eventMapSimulation.put("methodTestTime", "20:10:09");
        eventMapSimulation.put("localDate", "2020-10-09");
        eventMapSimulation.put("localDateTime", "2020-10-09T20:10:09.123456000000");
        eventMapSimulation.put("AUD_ENTTYP", "U");
        eventMapSimulation.put("AUD_APPLY_TIMESTAMP", "2020-10-09-20.10.09.123456");
    }

    @Test
    public void testProcessors() {
        RightEntityImplementation rightEntityImplementation = null;
        for (Listener listener : listenerList) {
            try {
                Object entityObject = listener.entityProcessor.getEntityClassInstance();
                listener.entityProcessor.getProcessorList().forEach(processor ->
                    processor.fieldNames.forEach(fieldName -> {
                        try {
                            if(eventMapSimulation.containsKey(fieldName)){
                                processor.process(entityObject, eventMapSimulation.get(fieldName));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            fail("Any exception may be not raised in parser");
                        }
                    }));
                rightEntityImplementation = (RightEntityImplementation) entityObject;
            } catch (IIdrApplicationException e) {
                e.printStackTrace();
                fail("Any excpetion may be not raised at this point");
            }
        }

        assertNotNull(rightEntityImplementation);
        assertEquals(STRING_FIELD_VALUE, rightEntityImplementation.string);
        assertEquals(1000000, rightEntityImplementation._long);
        assertEquals(10, rightEntityImplementation._int);
        assertEquals(10, rightEntityImplementation.methodTestInt);
        assertEquals(1.0, rightEntityImplementation._float,0);
        assertEquals(1.0, rightEntityImplementation._double, 0);
        assertEquals(BigDecimal.valueOf(1.0), rightEntityImplementation._bigDecimal);
        assertTrue(rightEntityImplementation._boolean);
        assertNotNull(rightEntityImplementation.date);
        assertNotNull(rightEntityImplementation.sqlDate);
        assertNotNull(rightEntityImplementation.time);
        assertNotNull(rightEntityImplementation.methodTestTime);
    }

}
