package br.com.viavarejo.iidr_event_processor_example;

import br.com.viavarejo.iidr_event_processor.application.IIdrApplication;
import br.com.viavarejo.iidr_event_processor.exceptions.EntityWrongImplementationException;
import br.com.viavarejo.iidr_event_processor.exceptions.ListenerWrongImplemetationException;

import java.util.Properties;

import static java.lang.String.format;

public class Main {
  public static void main(String[] args) throws ClassNotFoundException, EntityWrongImplementationException, ListenerWrongImplemetationException {
    final Properties consumerProps = new Properties();
    consumerProps.put("bootstrap.servers", "localhost:9092");
    IIdrApplication iIdrApplication = IIdrApplication.run(new EventListenersController(), consumerProps, (exception, consumerRecord) -> {
      //Example of handling a parser entity error
      System.out.println(format("handling json to entity error cause of value of topic <%s>:", consumerRecord.topic()));
      exception.printStackTrace();
      System.out.println(format("EventValue %s", consumerRecord.value()));
    });

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
