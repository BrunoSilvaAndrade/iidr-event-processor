package com.iidr_event_processor.processor;

import com.iidr_event_processor.exceptions.IIdrApplicationException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Set;

public abstract class Processor {
  final public boolean mayBeNull;
  final public boolean isCustomEntity;
  final public Set<String> fieldNames;
  final public EntityProcessor entityProcessor;
  final public IIdrValueParser iidrValueParser;

  Processor(IIdrValueParser iidrValueParser, boolean mayBeNull, Set<String> fieldNames, boolean isCustomEntity, EntityProcessor entityProcessor) {
    this.iidrValueParser = iidrValueParser;
    this.mayBeNull = mayBeNull;
    this.fieldNames = fieldNames;
    this.isCustomEntity = isCustomEntity;
    this.entityProcessor = entityProcessor;
  }

  public abstract void process(Object entityObject, String iidrValue) throws IIdrApplicationException;

  public void processCustomField(Object entityObject, Object customEntityObject) throws IIdrApplicationException {
    throw new NotImplementedException();
  }
}
