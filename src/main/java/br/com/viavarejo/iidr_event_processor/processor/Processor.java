package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;

import java.util.Set;

public abstract class Processor {
  final public boolean mayBeNull;
  final public boolean isCustomEntity;
  final public Set<String> fieldNames;
  final public EntityProcessor entityProcessor;
  final public FieldParser fieldParser;

  Processor(FieldParser fieldParser, boolean mayBeNull, Set<String> fieldNames, boolean isCustomEntity, EntityProcessor entityProcessor) {
    this.fieldParser = fieldParser;
    this.mayBeNull = mayBeNull;
    this.fieldNames = fieldNames;
    this.isCustomEntity = isCustomEntity;
    this.entityProcessor = entityProcessor;
  }

  public abstract void processCustomField(Object entityObject, Object customEntityObject) throws IIdrApplicationException;
  public abstract void process(Object entityObject, String iidrValue) throws IIdrApplicationException;
}
