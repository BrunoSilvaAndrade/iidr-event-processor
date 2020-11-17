package br.com.viavarejo.iidr_event_processor.utils;

import br.com.viavarejo.iidr_event_processor.annotations.IIDRAlias;
import br.com.viavarejo.iidr_event_processor.annotations.IIDRNonNull;
import br.com.viavarejo.iidr_event_processor.annotations.IIDRPattern;

import java.sql.Timestamp;

public abstract class IIdrEntity {
  @IIDRNonNull
  @IIDRAlias("AUD_ENTTYP")
  private IIdrOperationEnum operation;

  @IIDRNonNull
  @IIDRAlias("AUD_APPLY_TIMESTAMP")
  @IIDRPattern("yyyy-MM-dd-HH.mm.ss.SSSSSS")
  private Timestamp  operationTimestamp;

  public IIdrOperationEnum getOperation() {
    return operation;
  }

  public Timestamp getOperationTimestamp() {
    return operationTimestamp;
  }
}
