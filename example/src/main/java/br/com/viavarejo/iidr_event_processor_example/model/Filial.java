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
//    "NM_FIL": "NOVA FRIBURGO 2 - RJ                              "
//  }

//We can model the entity with like that below
public class Filial extends IIdrEntity {

  @IIDRNonNull
  @IIDRAlias("CD_EMPGCB")
  private Integer codigoEmpresa;

  //OR

  @IIDRNonNull
  private Integer CD_EMPGCB;

  @IIDRNonNull
  @IIDRAlias("CD_FIL")
  private Integer codigoFilial;

  //Fields of type like Date,Time,Timestamp needs @IIDRPattern Annotation with the pattern to parse the IIdr's values
  @IIDRAlias("DT_FIL_ING")
  @IIDRPattern("yyyy-MM-dd")
  private Date dataInauguracao;

  @IIDRNonNull
  @IIDRAlias("NM_FIL")
  private String nomeFilial;

  @IIDRIgnore
  private String nomeFilial2;

  @IIDRSetter("NM_FIL")
  public void setNomeFilial2(String nomeFilial2) {
    this.nomeFilial2 = nomeFilial2 + " nomeFilial2";
  }

  public String getNomeFilial2() {
    return nomeFilial2;
  }

  //The entity may contain a field which does not come from IIdr
  //In this case you can ignore this field
  @IIDRIgnore
  private String ignoredField;

  public Date getDataInauguracao() {
    return dataInauguracao;
  }


  //You can use others custom objects into an entity
  private Empresa empresa;

  public Empresa getEmpresa() {
    return empresa;
  }
}
