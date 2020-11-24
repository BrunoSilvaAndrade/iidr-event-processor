package br.com.viavarejo.iidr_event_processor.processor;

import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;

import java.lang.reflect.Method;
import java.util.Set;

import static java.lang.String.format;

public class MethodProcessor extends Processor {
  private final Method method;

  MethodProcessor(Method method, IIdrValueParser iidrValueParser, boolean mayBeNull, Set<String> fieldNames) {
    super(iidrValueParser, mayBeNull, fieldNames, false, null);
    this.method = method;
  }

  @Override
  public void process(Object entityObject, String iidrValue) throws IIdrApplicationException {
    try {
      method.invoke(entityObject, iidrValueParser.parse(iidrValue));
    }catch (Exception e) {
      throw new IIdrApplicationException(format("Fail to parse iidrValue %s to inject in method <%s> of class %s", iidrValue, method.getName(), entityObject.getClass().getCanonicalName()), e);
    }

  }
}
