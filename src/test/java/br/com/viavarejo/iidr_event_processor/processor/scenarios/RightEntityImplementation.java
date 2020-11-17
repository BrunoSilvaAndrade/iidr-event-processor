package br.com.viavarejo.iidr_event_processor.processor.scenarios;

import br.com.viavarejo.iidr_event_processor.annotations.IIDRAlias;
import br.com.viavarejo.iidr_event_processor.annotations.IIDRIgnore;
import br.com.viavarejo.iidr_event_processor.annotations.IIDRPattern;
import br.com.viavarejo.iidr_event_processor.annotations.IIDRSetter;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class RightEntityImplementation {
     //Enum to test the function enum parser;
    public enum SomeEnum{
        A;
     }

    //Testing if Ignore field will work when an unsupported is found
    @IIDRIgnore
    EntityWithTimeFieldsWithoutFormatAnnotation entityWithTimeFieldsWithoutFormatAnnotation;

    @IIDRAlias("string1")
    public String string;

    public long _long;

    public int _int;

    public  float _float;

    public double _double;

    public boolean _boolean;

    public BigDecimal _bigDecimal;

    @IIDRPattern("yyyy-MM-dd")
    public Date date;

    @IIDRPattern("yyyy-MM-dd")
    public java.sql.Date sqlDate;

    @IIDRPattern("HH:mm:ss")
    public Time time;

    @IIDRPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'000000'")
    public Timestamp timestamp;

    public SomeEnum someEnum;

    @IIDRIgnore
    public int methodTestInt;

    @IIDRIgnore
    public Time methodTestTime;

    @IIDRSetter("methodTest")
    private void setMethodTestInt(int methodTestInt) {
        this.methodTestInt = methodTestInt;
    }

    @IIDRSetter("methodTestTime")
    private void setMethodTestTime(@IIDRPattern("HH:mm:ss") Time methodTestTime) {
        this.methodTestTime = methodTestTime;
    }

}
