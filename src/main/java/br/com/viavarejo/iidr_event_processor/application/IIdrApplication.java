package br.com.viavarejo.iidr_event_processor.application;

import br.com.viavarejo.iidr_event_processor.exceptions.*;
import br.com.viavarejo.iidr_event_processor.processor.EntityProcessor;
import br.com.viavarejo.iidr_event_processor.processor.Listener;
import br.com.viavarejo.iidr_event_processor.processor.ListenersProcessorFactory;
import br.com.viavarejo.iidr_event_processor.utils.ConsumerFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Objects.nonNull;

public class IIdrApplication {
  private static final int DEFAULT_REMAINING_RETRIES = 10;

  private final IIdrEntityParserErrorCallback callback;
  private ExecutorService executors;
  private final List<Listener> listenerList;
  private final Properties kafkaConsumerProperties;
  private final int remainingRetries;

  private boolean shutdownRequested = false;
  private boolean hasError = false;


  private IIdrApplication(final List<Listener> listenerList, final Properties kafkaConsumerProperties, final IIdrEntityParserErrorCallback callback, final int remainingRetries) {
    this.remainingRetries = remainingRetries;
    this.listenerList = listenerList;
    this.kafkaConsumerProperties = kafkaConsumerProperties;
    this.callback = callback;
  }

  public static IIdrApplication run(
      final Object listenerControllerObject,
      final Properties kafkaConsumerProperties,
      final IIdrEntityParserErrorCallback callback,
      final int remainingRetries
  ) throws ClassNotFoundException, EntityWrongImplementationException, ListenerWrongImplemetationException {
    final IIdrApplication iidrApplication = new IIdrApplication(ListenersProcessorFactory.getListeners(listenerControllerObject), kafkaConsumerProperties, callback, remainingRetries);
    iidrApplication.run();
    return iidrApplication;
  }

  public static IIdrApplication run(final Object listenerControllerObject, final  Properties kafkaConsumerProperties) throws ClassNotFoundException, EntityWrongImplementationException, ListenerWrongImplemetationException {
    return run(listenerControllerObject, kafkaConsumerProperties, (e,cr)->{throw e;}, DEFAULT_REMAINING_RETRIES);
  }

  public static IIdrApplication run(final Object listenerControllerObject, final Properties kafkaConsumerProperties, final IIdrEntityParserErrorCallback callback) throws EntityWrongImplementationException, ListenerWrongImplemetationException, ClassNotFoundException {
    return run(listenerControllerObject, kafkaConsumerProperties, callback, DEFAULT_REMAINING_RETRIES);
  }

  private void run(){
    if(!listenerList.isEmpty()){
      executors = Executors.newFixedThreadPool(listenerList.size());
      for(Listener listener: listenerList) {
        executors.execute(() -> {
          final JSONParser jsonParser = new JSONParser();
          final EntityProcessor entityProcessor = listener.entityProcessor;
          final Consumer<String, String> consumer = ConsumerFactory.getConsumer(listener.getKafkaListenerAnnotation(), kafkaConsumerProperties);

          List<ConsumerRecord<String, String>> records = new ArrayList<>();
          List<Object> entityObjectList = new ArrayList<>();
          int remainingRetries = this.remainingRetries;

          while (!shutdownRequested) {
            try {
              //If nonEmpty is because is a retry
              //If empty is because it was successful handled
              if (records.isEmpty()) {
                for (ConsumerRecord<String, String> consumerRecord : consumer.poll(Duration.ofMillis(100))) {
                  records.add(consumerRecord);
                }
                if(records.isEmpty())
                  continue;
              }

              for (ConsumerRecord<String, String> record : records) {
                try {
                  final JSONObject jsonObject = (JSONObject) jsonParser.parse(record.value());
                  entityObjectList.add(entityProcessor.bind(jsonObject));
                }catch (IIdrApplicationException | FieldMayBeNotNullException e) {
                  callback.call(e,record);
                }catch (ParseException pe){
                  callback.call(new WrongJsonSyntaxException(pe), record);
                }
              }
              listener.injectEntityObjectList(entityObjectList);
            } catch (Exception e) {
              e.printStackTrace();
              if (remainingRetries == 0) {
                hasError = true;
                shutdownRequested = true;
              } else {
                remainingRetries--;
              }
              continue;
            }

            consumer.commitAsync();
            records.clear();
            entityObjectList.clear();
            remainingRetries = this.remainingRetries;
          }
          close();
        });
      }
    }
  }

  public void close() {
    shutdownRequested = true;
    if(nonNull(executors)) {
      executors.shutdown();
    }
  }

  public boolean terminatedWithError(){
    return hasError;
  }

  public boolean isRunning(){
    return nonNull(executors) && !executors.isTerminated();
  }
}
