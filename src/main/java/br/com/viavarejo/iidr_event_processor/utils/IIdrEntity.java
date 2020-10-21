package br.com.viavarejo.iidr_event_processor.utils;

import br.com.viavarejo.iidr_event_processor.annotations.Alias;
import br.com.viavarejo.iidr_event_processor.annotations.NonNull;
import br.com.viavarejo.iidr_event_processor.annotations.Pattern;

import java.sql.Timestamp;

public abstract class IIdrEntity {
  @NonNull
  @Alias("AUD_ENTTYP")
  private IIdrOperationEnum operation;

  @NonNull
  @Alias("AUD_APPLY_TIMESTAMP")
  @Pattern("yyyy-MM-dd-HH.mm.ss.nnnnnn")
  private Timestamp  operationTimestamp;

  public IIdrOperationEnum getOperation() {
    return operation;
  }

  public Timestamp getOperationTimestamp() {
    return operationTimestamp;
  }
}
