package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;

import java.util.List;

public class EntityProcessor {
  private final Class<?> entityClass;
  private final List<Processor> fieldProcessorList;

  public EntityProcessor(Class<?> entityClass, List<Processor> fieldProcessorList) {
    this.entityClass = entityClass;
    this.fieldProcessorList = fieldProcessorList;
  }

  public Object getEntityClassInstance() throws IIdrApplicationException {
    try {
      return entityClass.newInstance();
    } catch (IllegalAccessException | InstantiationException e) {
      throw new IIdrApplicationException(String.format("Fail to create a new instance of entity Class <%s> cause: %s", entityClass.getCanonicalName(), e.getMessage()));
    }
  }

  public List<Processor> getFieldProcessorList() {
    return fieldProcessorList;
  }
}
