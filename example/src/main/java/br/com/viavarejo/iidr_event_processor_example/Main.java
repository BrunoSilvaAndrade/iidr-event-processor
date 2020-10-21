package br.com.viavarejo.iidr_event_processor_example;

import br.com.viavarejo.iidr_event_processor.application.IIdrApplication;
import br.com.viavarejo.iidr_event_processor.exceptions.EntityWrongImplementationException;
import br.com.viavarejo.iidr_event_processor.exceptions.ListenerWrongImplemetationException;

import java.util.Properties;

public class Main {
  public static void main(String[] args) throws ClassNotFoundException, EntityWrongImplementationException, ListenerWrongImplemetationException {
    final Properties consumerProps = new Properties();
    consumerProps.put("bootstrap.servers", "localhost:9092");
    IIdrApplication.run(new EventListenersController(), consumerProps);
  }
}
