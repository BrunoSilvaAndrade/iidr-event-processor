package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;

import java.util.List;

import static java.util.Objects.nonNull;

public class EntityProcessor {
  private final Class<?> entityClass;
  private final List<Processor> processorList;
  private final NonMappedFieldProcessor nonMappedFieldProcessor;

  public EntityProcessor(Class<?> entityClass, List<Processor> processorList, NonMappedFieldProcessor nonMappedFieldProcessor) {
    this.entityClass = entityClass;
    this.processorList = processorList;
    this.nonMappedFieldProcessor = nonMappedFieldProcessor;
  }

  public EntityProcessor(Class<?> entityClass, List<Processor> processorList) {
    this(entityClass, processorList, null);
  }

  public Object getEntityClassInstance() throws IIdrApplicationException {
    try {
      return entityClass.newInstance();
    } catch (IllegalAccessException | InstantiationException e) {
      throw new IIdrApplicationException(String.format("Fail to create a new instance of entity Class <%s> cause: %s", entityClass.getCanonicalName(), e.getMessage()));
    }
  }

  public List<Processor> getProcessorList() {
    return processorList;
  }

  public boolean hasNonMappedFieldProcessor(){
    return nonNull(nonMappedFieldProcessor);
  }

  public NonMappedFieldProcessor getNonMappedFieldProcessor() {
    return nonMappedFieldProcessor;
  }
}
