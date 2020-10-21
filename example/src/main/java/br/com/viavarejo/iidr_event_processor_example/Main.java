package br.com.viavarejo.iidr_event_processor_example;

import br.com.viavarejo.iidr_event_processor.application.IIdrApplication;
import br.com.viavarejo.iidr_event_processor.exceptions.EntityWrongImplementationException;
import br.com.viavarejo.iidr_event_processor.exceptions.ListenerWrongImplemetationException;

import java.util.Properties;

public class Main {
  public static void main(String[] args) throws ClassNotFoundException, EntityWrongImplementationException, ListenerWrongImplemetationException {
    final Properties consumerProps = new Properties();
    consumerProps.put("bootstrap.servers", "localhost:9092");
    IIdrApplication iIdrApplication = IIdrApplication.run(new EventListenersController(), consumerProps);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      boolean hasError;
      try {
          iIdrApplication.close();
          //Waiting task close until 5000 ms
          Thread.sleep(2000);
          hasError = iIdrApplication.terminatedWithError();
      } catch (Exception e) {
        hasError = true;
      }
      Runtime.getRuntime().halt(hasError?1:0);
    }));
  }
}
