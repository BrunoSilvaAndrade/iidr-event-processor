package br.com.viavarejo.iidr_event_processor_example;

import br.com.viavarejo.iidr_event_processor.annotations.KafkaListerner;
import br.com.viavarejo.iidr_event_processor_example.model.Filial;

import java.util.List;

public class EventListenersController {

  @KafkaListerner(id="someId", topics = {"listOfTopics"})
  public void someWorker(List<Filial> filialList){
    //When any event is produced of any topic of that topic list in annotation this method will be invoked with a list of your entity with type parsed already
    //now you can do anything with your entityList
  }

}