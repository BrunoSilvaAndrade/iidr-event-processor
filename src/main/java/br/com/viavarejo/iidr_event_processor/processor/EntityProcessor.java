package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;

import java.util.List;

public class EntityProcessor {
  private final Class<?> entityClass;
  private final List<Processor> processorList;

  public EntityProcessor(Class<?> entityClass, List<Processor> processorList) {
    this.entityClass = entityClass;
    this.processorList = processorList;
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
}
