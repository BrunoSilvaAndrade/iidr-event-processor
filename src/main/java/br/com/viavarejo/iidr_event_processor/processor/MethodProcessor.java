package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Method;
import java.util.Set;

import static java.lang.String.format;

public class MethodProcessor extends Processor {
  private final Method method;

  MethodProcessor(Method method, FieldParser fieldParser, boolean mayBeNull, Set<String> fieldNames) {
    super(fieldParser, mayBeNull, fieldNames, false, null);
    this.method = method;
  }

  @Override
  public void processCustomField(Object entityObject, Object customEntityObject){
    throw new NotImplementedException();
  }

  @Override
  public void process(Object entityObject, String iidrValue) throws IIdrApplicationException {
    try {
      method.invoke(entityObject, fieldParser.parse(iidrValue));
    }catch (Exception e) {
      throw new IIdrApplicationException(format("Fail to parse iidrValue %s to inject in method <%s> of class %s", iidrValue, method.getName(), entityObject.getClass().getCanonicalName()), e);
    }

  }
}
