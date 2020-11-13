package br.com.viavarejo.iidr_event_processor.application;

import br.com.viavarejo.iidr_event_processor.annotations.KafkaListerner;
import br.com.viavarejo.iidr_event_processor.exceptions.*;
import br.com.viavarejo.iidr_event_processor.processor.EntityProcessor;
import br.com.viavarejo.iidr_event_processor.processor.FieldProcessor;
import br.com.viavarejo.iidr_event_processor.processor.Listener;
import br.com.viavarejo.iidr_event_processor.processor.ListenersProcessorFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class IIdrApplication {
  private static final int DEFAULT_REMAINING_RETRIES = 10;

  private final IIdrEntityParserErrorCallback callback;
  private ExecutorService executors;
  private final List<Listener> listenerList;
  private final Properties kafkaConsumerProperties;
  private final Object listenerControllerObject;
  private final int remainingRetries;

  private boolean shutdownHandled = false;
  private boolean hasError = false;


  private IIdrApplication(final List<Listener> listenerList, final Object listenerControllerObject, final Properties kafkaConsumerProperties, final IIdrEntityParserErrorCallback callback, final int remainingRetries) {
    this.remainingRetries = remainingRetries;
    this.listenerList = listenerList;
    this.kafkaConsumerProperties = kafkaConsumerProperties;
    this.listenerControllerObject = listenerControllerObject;
    this.callback = callback;
  }

  public static IIdrApplication run(
      final Object listenerControllerObject,
      final Properties kafkaConsumerProperties,
      final IIdrEntityParserErrorCallback callback,
      final int remainingRetries
  ) throws ClassNotFoundException, EntityWrongImplementationException, ListenerWrongImplemetationException {
    final IIdrApplication iidrApplication = new IIdrApplication(ListenersProcessorFactory.getListeners(listenerControllerObject), listenerControllerObject, kafkaConsumerProperties, callback, remainingRetries);
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
          final Method method = listener.method;
          final JSONParser jsonParser = new JSONParser();
          final EntityProcessor entityProcessor = listener.entityProcessor;
          final Consumer<String, String> consumer = getConsumer(method.getDeclaredAnnotation(KafkaListerner.class), kafkaConsumerProperties);

          List<ConsumerRecord<String, String>> records = new ArrayList<>();
          List<Object> entityObjectList = new ArrayList<>();
          int remainingRetries = this.remainingRetries;

          while (!shutdownHandled) {
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
                  final Object entityObject = mapObject(entityProcessor, jsonObject);
                  entityObjectList.add(entityObject);
                }catch (IIdrApplicationException | FieldMayBeNotNullException e) {
                  callback.call(e,record);
                }catch (ParseException pe){
                  callback.call(new WrongJsonSyntaxException(pe), record);
                }
              }
              method.invoke(listenerControllerObject, entityObjectList);
            } catch (Exception e) {
              e.printStackTrace();
              if (remainingRetries == 0) {
                hasError = true;
                shutdownHandled = true;
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

  private Object mapObject(EntityProcessor entityProcessor, JSONObject jsonObject) throws IIdrApplicationException, FieldMayBeNotNullException{
    final Object entityObject = entityProcessor.getEntityClassInstance();
    for (FieldProcessor fieldProcessor : entityProcessor.getFieldProcessorList()) {
      if(fieldProcessor.isCustomEntity){
        fieldProcessor.processCustomField(entityObject, mapObject(fieldProcessor.entityProcessor, jsonObject));
        continue;
      }
      final String iidrValue = tryFindIIdrValue(fieldProcessor.fieldNames, jsonObject);
      if(isNull(iidrValue) && !fieldProcessor.mayBeNull) {
        throw new FieldMayBeNotNullException(format("Field <%s> of <%s> was not found or its value is null in the event", fieldProcessor.getNativeFieldName(), entityObject.getClass().getCanonicalName()));
      }else if(nonNull(iidrValue)){
        fieldProcessor.proccessField(entityObject, iidrValue.trim());
      }
    }
    return entityObject;
  }

  private static Consumer<String, String> getConsumer(KafkaListerner kafkaListerner, Properties consumerProps) {
    final Properties props = new Properties();
    props.putAll(consumerProps);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaListerner.id());
    final Consumer<String, String> consumer = new KafkaConsumer<>(props, new StringDeserializer(), new StringDeserializer());
    consumer.subscribe(asList(kafkaListerner.topics()));
    return consumer;
  }

  private static String tryFindIIdrValue(Set<String> fieldNames, JSONObject jsonObject) {
    for (String fieldName : fieldNames) {
      final String value = (String) jsonObject.get(fieldName);
      if(nonNull(value))
        return value;
    }
    return null;
  }

  public void close() {
    shutdownHandled = true;
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
