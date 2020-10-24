package br.com.viavarejo.iidr_event_processor.processor.scenarios;

import br.com.viavarejo.iidr_event_processor.annotations.Alias;
import br.com.viavarejo.iidr_event_processor.annotations.Ignore;
import br.com.viavarejo.iidr_event_processor.annotations.Pattern;

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

    @Pattern("yyyy-MM-dd")
    public Date date;

    @Pattern("yyyy-MM-dd")
    public java.sql.Date sqlDate;

    @Pattern("HH:mm:ss")
    public Time time;

    @Pattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'000000'")
    public Timestamp timestamp;

    public SomeEnum someEnum;
}
