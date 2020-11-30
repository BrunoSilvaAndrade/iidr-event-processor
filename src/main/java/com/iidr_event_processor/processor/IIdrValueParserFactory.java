package com.iidr_event_processor.processor;

import com.iidr_event_processor.annotations.IIDRPattern;
import com.iidr_event_processor.exceptions.EntityWrongImplementationException;
import com.iidr_event_processor.exceptions.IIdrApplicationException;
import com.iidr_event_processor.exceptions.UnsupportedTypeException;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public abstract class IIdrValueParserFactory {

  private static final IIdrValueParser STRING = (v) -> v;
  private static final IIdrValueParser LONG = Long::parseLong;
  private static final IIdrValueParser INT = Integer::parseInt;
  private static final IIdrValueParser FLOAT = Float::parseFloat;
  private static final IIdrValueParser DOUBLE = Double::parseDouble;
  private static final IIdrValueParser BOOLEAN = Boolean::parseBoolean;
  private static final IIdrValueParser BIGDECIMAL = BigDecimal::new;

  public static IIdrValueParser getParser(Class<?> type, IIDRPattern IIDRPattern) throws UnsupportedTypeException, EntityWrongImplementationException {

    if(String.class.equals(type)) {
      return STRING;
    }

    if (long.class.equals(type) || Long.class.equals(type)) {
      return LONG;
    }

    if(int.class.equals(type) || Integer.class.equals(type)) {
      return INT;
    }

    if(float.class.equals(type) || Float.class.equals(type)) {
      return FLOAT;
    }

    if(double.class.equals(type) || Double.class.equals(type)) {
      return DOUBLE;
    }

    if(boolean.class.equals(type) || Boolean.class.equals(type)) {
      return BOOLEAN;
    }

    if(BigDecimal.class.equals(type)) {
      return BIGDECIMAL;
    }

    if(Date.class.equals(type)) {
      final String strPattern = getStrPattern(IIDRPattern);
      return (v) -> Date.from(LocalDate.parse(v, DateTimeFormatter.ofPattern(strPattern)).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    if(java.sql.Date.class.equals(type)){
      final String strPattern = getStrPattern(IIDRPattern);
      return (v)  -> java.sql.Date.valueOf(LocalDate.parse(v, DateTimeFormatter.ofPattern(strPattern)));
    }

    if(Time.class.equals(type)) {
      final String strPattern = getStrPattern(IIDRPattern);
      return (v) -> Time.valueOf(LocalTime.parse(v, DateTimeFormatter.ofPattern(strPattern)));
    }

    if(Timestamp.class.equals(type)) {
      final String strPattern = getStrPattern(IIDRPattern);
      return (v) -> Timestamp.valueOf(LocalDateTime.parse(v, DateTimeFormatter.ofPattern(strPattern)));
    }

    if(LocalDate.class.equals(type)) {
      final String strPattern = getStrPattern(IIDRPattern);
      return (v) -> LocalDate.parse(v, DateTimeFormatter.ofPattern(strPattern));
    }

    if(LocalDateTime.class.equals(type)){
      final String strPattern = getStrPattern(IIDRPattern);
      return (v) ->  LocalDateTime.parse(v, DateTimeFormatter.ofPattern(strPattern));
    }

    if(type.isEnum()) {
      return (v) -> {
        for (Object enumField : type.getEnumConstants()) {
          if(((Enum<?>)enumField).name().equals(v)) {
            return enumField;
          }
        }
        throw new IIdrApplicationException(format("Value <%s> is not present in enum <%s>", v, type.getCanonicalName()));
      };
    }

    throw new UnsupportedTypeException(format("IIdrApplication does not support type <%s>", type.getCanonicalName()));
  }

  private static String getStrPattern(IIDRPattern IIDRPattern) throws EntityWrongImplementationException {
    if(isNull(IIDRPattern))
      throw new EntityWrongImplementationException(format("Annotation Pattern is needed"));
    return IIDRPattern.value();
  }
}
