package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.exceptions.EntityWrongImplementationException;
import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;
import br.com.viavarejo.iidr_event_processor.exceptions.ListenerWrongImplemetationException;
import br.com.viavarejo.iidr_event_processor.processor.scenarios.RightControllerImplementation;
import br.com.viavarejo.iidr_event_processor.processor.scenarios.RightEntityImplementation;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class FieldProcessorTest {
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
        eventMapSimulation.put("_float", "1.0");
        eventMapSimulation.put("_double", "1.0");
        eventMapSimulation.put("_boolean", "true");
        eventMapSimulation.put("_bigDecimal", "1.0");
        eventMapSimulation.put("date", "2020-10-09");
        eventMapSimulation.put("sqlDate", "2020-10-09");
        eventMapSimulation.put("time", "20:10:09");
        eventMapSimulation.put("timestamp", "2020-10-09T20:10:09.123456000000");
        eventMapSimulation.put("localDate", "2020-10-09");
        eventMapSimulation.put("localDateTime", "2020-10-09T20:10:09.123456000000");
        eventMapSimulation.put("someEnum", "A");
    }


    @Test
    public void testFieldProcessorParsers() {
        RightEntityImplementation rightEntityImplementation = null;
        for (Listener listener : listenerList) {
            try {
                Object entityObject = listener.entityProcessor.getEntityClassInstance();
                listener.entityProcessor.getFieldProcessorList().forEach(fieldProcessor ->
                    fieldProcessor.fieldNames.forEach(fieldName -> {
                        try {
                            fieldProcessor.proccessField(entityObject, eventMapSimulation.get(fieldName));
                        } catch (Exception e) {
                            fail("Any exception may be not raised in parser");
                        }
                    }));
                rightEntityImplementation = (RightEntityImplementation) entityObject;
            } catch (IIdrApplicationException e) {
                fail("Any excpetion may be not raised at this point");
            }
        }

        assertNotNull(rightEntityImplementation);
        assertEquals(STRING_FIELD_VALUE, rightEntityImplementation.string);
        assertEquals(1000000, rightEntityImplementation._long);
        assertEquals(10, rightEntityImplementation._int);
        assertEquals(1.0, rightEntityImplementation._float,0);
        assertEquals(1.0, rightEntityImplementation._double, 0);
        assertEquals(BigDecimal.valueOf(1.0), rightEntityImplementation._bigDecimal);
        assertTrue(rightEntityImplementation._boolean);
        assertNotNull(rightEntityImplementation.date);
        assertNotNull(rightEntityImplementation.sqlDate);
        assertNotNull(rightEntityImplementation.time);
        assertNotNull(rightEntityImplementation.timestamp);
        assertEquals(RightEntityImplementation.SomeEnum.A, rightEntityImplementation.someEnum);
    }

}
