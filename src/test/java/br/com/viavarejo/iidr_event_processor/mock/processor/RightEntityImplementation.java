package br.com.viavarejo.iidr_event_processor.mock.processor;

import br.com.viavarejo.iidr_event_processor.annotations.Alias;
import br.com.viavarejo.iidr_event_processor.annotations.Format;
import br.com.viavarejo.iidr_event_processor.annotations.Ignore;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class RightEntityImplementation {
     //Enum to test the function enum parser;
    public enum SomeEnum{
        A;
     }

    //Testing if Ignore field will work when an unsupported is found
    @Ignore
    EntityWithTimeFieldsWithoutFormatAnnotation entityWithTimeFieldsWithoutFormatAnnotation;

    @Alias("string1")
    public String string;

    public long _long;

    public int _int;

    public  float _float;

    public double _double;

    public boolean _boolean;

    @Format("yyyy-MM-dd")
    public Date date;

    @Format("yyyy-MM-dd")
    public java.sql.Date sqlDate;

    @Format("HH:mm:ss")
    public Time time;

    @Format("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'000000'")
    public Timestamp timestamp;

    public SomeEnum someEnum;
}
