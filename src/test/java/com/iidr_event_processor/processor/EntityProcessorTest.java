package com.iidr_event_processor.processor;

import com.iidr_event_processor.exceptions.EntityWrongImplementationException;
import com.iidr_event_processor.exceptions.FieldMayBeNotNullException;
import com.iidr_event_processor.exceptions.IIdrApplicationException;
import com.iidr_event_processor.exceptions.ListenerWrongImplemetationException;
import com.iidr_event_processor.processor.scenarios.RightControllerImplementation;
import com.iidr_event_processor.processor.scenarios.RightEntityImplementation;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class EntityProcessorTest {
    private static final String STRING_FIELD_VALUE = "valor1";
    JSONObject jsonObject;
    List<Listener> listenerList;

    @Before
    public void init() throws ClassNotFoundException, EntityWrongImplementationException, ListenerWrongImplemetationException {
        listenerList = ListenersProcessorFactory.getListeners(new RightControllerImplementation());
        jsonObject = new JSONObject();
        jsonObject.put("string1", STRING_FIELD_VALUE);
        jsonObject.put("_long", "1000000");
        jsonObject.put("_int", "10");
        jsonObject.put("methodTest", "10");
        jsonObject.put("_float", "1.0");
        jsonObject.put("_double", "1.0");
        jsonObject.put("_boolean", "true");
        jsonObject.put("_bigDecimal", "1.0");
        jsonObject.put("date", "2020-10-09");
        jsonObject.put("sqlDate", "2020-10-09");
        jsonObject.put("time", "20:10:09");
        jsonObject.put("methodTestTime", "20:10:09");
        jsonObject.put("localDate", "2020-10-09");
        jsonObject.put("localDateTime", "2020-10-09T20:10:09.123456000000");
        jsonObject.put("AUD_ENTTYP", "U");
        jsonObject.put("AUD_APPLY_TIMESTAMP", "2020-10-09-20.10.09.123456");
        jsonObject.put("NON_MAPPED_VALUE", "");
    }

    @Test
    public void testProcessors() {
        RightEntityImplementation rightEntityImplementation = null;

        for (Listener listener : listenerList) {
            try {
                rightEntityImplementation = (RightEntityImplementation) listener.entityProcessor.bind(jsonObject);
            } catch (IIdrApplicationException | FieldMayBeNotNullException e) {
                e.printStackTrace();
                fail("Any exception may be not raised when IIdrObjectMapper.mapObject is called");
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
        assertTrue(rightEntityImplementation.hasNonMappedFields());
    }

}
