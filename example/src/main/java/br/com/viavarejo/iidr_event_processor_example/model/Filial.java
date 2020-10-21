package br.com.viavarejo.iidr_event_processor_example.model;

import br.com.viavarejo.iidr_event_processor.annotations.Alias;
import br.com.viavarejo.iidr_event_processor.annotations.Ignore;
import br.com.viavarejo.iidr_event_processor.annotations.NonNull;
import br.com.viavarejo.iidr_event_processor.annotations.Pattern;
import br.com.viavarejo.iidr_event_processor.utils.IIdrEntity;

import java.sql.Date;

//If we receive an event like this {
//    "AUD_ENTTYP": "U",
//    "AUD_APPLY_TIMESTAMP": "2020-10-17-00.38.09.025232",
//    "CD_EMPGCB": "21",
//    "CD_FIL": "119",
//    "DT_FIL_ING": "2017-02-17",
//    "NM_FIL": "NOVA FRIBURGO 2 - RJ                              "
//  }

//We can model the entity with like that below
public class Filial extends IIdrEntity {

  @NonNull
  @Alias("CD_EMPGCB")
  private Integer codigoEmpresa;

  //OR

  @NonNull
  private Integer CD_EMPGCB;

  @NonNull
  @Alias("CD_FIL")
  private Integer codigoFilial;

  //Fields of type like Date,Time,Timestamp needs @Pattern Annotation with the pattern to parse the IIdr's values
  @Alias("DT_FIL_ING")
  @Pattern("yyyy-MM-dd")
  private Date dataInauguracao;

  @NonNull
  @Alias("NM_FIL")
  private String nomeFilial;

  //The entity may contain a field which does not come from IIdr
  //In this case you can ignore this field
  @Ignore
  private String ignoredField;


  //You can use others custom objects into an entity
}
