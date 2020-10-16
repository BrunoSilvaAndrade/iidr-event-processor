package br.com.viavarejo.iidr_event_processor.application;

import br.com.viavarejo.iidr_event_processor.annotations.KafkaListerner;
import br.com.viavarejo.iidr_event_processor.exceptions.FieldMayBeNotNullException;
import br.com.viavarejo.iidr_event_processor.exceptions.IIdrApplicationException;
import br.com.viavarejo.iidr_event_processor.processor.EntityProcessor;
import br.com.viavarejo.iidr_event_processor.processor.FieldProcessor;
import br.com.viavarejo.iidr_event_processor.processor.ListenersProcessor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.simple.parser.JSONParser;

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
  private ExecutorService executors;
  private final Map<Method, EntityProcessor> entityProcessorMap;
  private final Properties properties;
  private final Object listenerControllerObject;

  private boolean shutdownHandled = false;
  private boolean hasError = false;


  IIdrApplication(Map<Method, EntityProcessor> entityProcessorMap, Object listenerControllerObject, Properties properties) {
    this.entityProcessorMap = entityProcessorMap;
    this.properties = properties;
    this.listenerControllerObject = listenerControllerObject;
  }

  public static IIdrApplication run(final Object listenerControllerObject, Properties properties) throws IIdrApplicationException {
    final IIdrApplication iidrApplication = new IIdrApplication(ListenersProcessor.getListenersMap(listenerControllerObject), listenerControllerObject, properties);
    iidrApplication.run();
    return iidrApplication;
  }


  private void run(){
    if(!entityProcessorMap.isEmpty()){
      executors = Executors.newFixedThreadPool(entityProcessorMap.size());
      for(Map.Entry<Method, EntityProcessor> entry: entityProcessorMap.entrySet()) {
        final Method method = entry.getKey();
        final EntityProcessor entityProcessor = entry.getValue();
        final Consumer<String, String> consumer = getConsumer(method.getDeclaredAnnotation(KafkaListerner.class), properties);

        executors.execute(() -> {
          final JSONParser jsonParser = new JSONParser();
          int remainingRetries = 10;
          List<ConsumerRecord<String, String>> records = new ArrayList<>();
          List<Object> entityObjectList = new ArrayList<>();

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
                final Map<String, String> jsonValueMap = (Map<String, String>) jsonParser.parse(record.value());
                final Object entityObject = entityProcessor.getEntityClassInstace();

                for (FieldProcessor fieldProcessor : entityProcessor.getFieldProcessorList()) {
                  final String iidrValue = tryFindIIdrValue(fieldProcessor.fieldNames, jsonValueMap);
                  if(isNull(iidrValue) && !fieldProcessor.mayBeNull) {
                    throw new FieldMayBeNotNullException(format("Field <%s> of <%s> was not found or its value is null in %s", fieldProcessor.getNativeFieldName(), entityObject.getClass().getCanonicalName(), record.value()));
                  }else if(nonNull(iidrValue)){
                    fieldProcessor.proccessField(entityObject, iidrValue.trim());
                  }
                }
                entityObjectList.add(entityObject);
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
            remainingRetries = 10;
          }
          close();
        });
      }
    }
  }

  private static Consumer<String, String> getConsumer(KafkaListerner kafkaListerner, Properties properties) {
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaListerner.id());
    final Consumer<String, String> consumer = new KafkaConsumer<>(properties, new StringDeserializer(), new StringDeserializer());
    consumer.subscribe(asList(kafkaListerner.topics()));
    return consumer;
  }

  private static String tryFindIIdrValue(Set<String> fieldNames, Map<String, String> jsonValueMap) {
    for (String fieldName : fieldNames) {
      final String value = jsonValueMap.get(fieldName);
      if(nonNull(value))
        return value;
    }
    return null;
  }

  public void close() {
    shutdownHandled = true;
    executors.shutdown();
  }

  public boolean terminatedWithError(){
    return hasError;
  }

  public boolean isRunning(){
    return nonNull(executors) && !executors.isTerminated();
  }
}
