package br.com.viavarejo.iidr_event_processor_example.model;

import br.com.viavarejo.iidr_event_processor.annotations.*;
import br.com.viavarejo.iidr_event_processor.utils.IIdrEntity;

import java.sql.Date;

//If we receive an event like this {
//    "AUD_ENTTYP": "U",
//    "AUD_APPLY_TIMESTAMP": "2020-10-17-00.38.09.025232",
//    "CD_EMPGCB": "21",
//    "CD_FIL": "119",
//    "DT_FIL_ING": "2017-02-17",
//    "NM_FIL": "NOVA FRIBURGO 2 - RJ                              ",
//    "NON_MAPPED_FIELD": "SOME_VALUE"
//  }

//We can model the entity with like that below
public class Filial extends IIdrEntity {

  //You can use others custom objects into an entity
  private Empresa empresa;

  @IIDRNonNull
  @IIDRAlias("CD_FIL")
  private Integer codigoFilial;

  //Fields of type like Date,Time,Timestamp needs @IIDRPattern Annotation with the pattern to parse the IIdr's values
  @IIDRAlias("DT_FIL_ING")
  @IIDRPattern("yyyy-MM-dd")
  private Date dataInauguracao;

  @IIDRIgnore
  private String nomeFilial;

  @IIDRSetter("NM_FIL")
  public void setNomeFilial(@IIDRNonNull String nomeFilial) {
    this.nomeFilial = nomeFilial + " Using IIDRSetter";
  }

  public String getNomeFilial() {
    return nomeFilial;
  }

  //The entity may contain a field which does not come from IIdr
  //In this case you can ignore this field
  @IIDRIgnore
  private String ignoredField;

  public Date getDataInauguracao() {
    return dataInauguracao;
  }

  public Empresa getEmpresa() {
    return empresa;
  }
}
