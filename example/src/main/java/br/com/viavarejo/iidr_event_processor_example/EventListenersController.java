package br.com.viavarejo.iidr_event_processor_example;

import br.com.viavarejo.iidr_event_processor.annotations.KafkaListerner;
import br.com.viavarejo.iidr_event_processor_example.model.Filial;

import java.util.List;

public class EventListenersController {

  @KafkaListerner(id="someId", topics = {"some.topic"})
  public void someWorker(List<Filial> filialList){
    System.out.println("List size: "+ filialList.size());
    filialList.forEach(filial -> {
      System.out.println("IIDR operation : " + filial.getOperation());
      System.out.println("IIDR operation timestamp : " + filial.getOperationTimestamp());
      System.out.println("Filial data Inauguracao : " + filial.getDataInauguracao());
      System.out.println("Empresa: "+filial.getEmpresa().getCodigo());
      System.out.println("Test method IIDRSetter : " + filial.getNomeFilial());
      System.out.println("Has NonMappedFields : " + filial.hasNonMappedFields());
      if(filial.hasNonMappedFields()) {
        System.out.println("NonMappedFields : " + filial.getNonMappedFields().toString());
      }
      System.out.println();
    });
    //When any event is produced of any topic of that topic list in annotation this method will be invoked with a list of your entity with type parsed already
    //now you can do anything with your entityList
  }

}
