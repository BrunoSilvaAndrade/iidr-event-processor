package br.com.viavarejo.iidr_event_processor.utils;

import br.com.viavarejo.iidr_event_processor.annotations.IIDRAlias;
import br.com.viavarejo.iidr_event_processor.annotations.IIDRNonNull;
import br.com.viavarejo.iidr_event_processor.annotations.IIDRPattern;
import br.com.viavarejo.iidr_event_processor.annotations.NonMappedFields;

import java.sql.Timestamp;
import java.util.Map;

import static java.util.Objects.nonNull;

public abstract class IIdrEntity {
  @IIDRNonNull
  @IIDRAlias("AUD_ENTTYP")
  private IIdrOperationEnum operation;

  @IIDRNonNull
  @IIDRAlias("AUD_APPLY_TIMESTAMP")
  @IIDRPattern("yyyy-MM-dd-HH.mm.ss.SSSSSS")
  private Timestamp  operationTimestamp;

  @NonMappedFields
  private Map<String, String> nonMappedFields;

  public IIdrOperationEnum getOperation() {
    return operation;
  }

  public Timestamp getOperationTimestamp() {
    return operationTimestamp;
  }

  public boolean hasNonMappedFields() {
    return nonNull(nonMappedFields);
  }

  public Map<String, String> getNonMappedFields() {
    return nonMappedFields;
  }
}
