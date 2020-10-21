package br.com.viavarejo.iidr_event_processor.exceptions;

public class IIdrApplicationException extends Exception {
  public IIdrApplicationException(String msg) {
    super(msg);
  }

  public IIdrApplicationException(String msg, Throwable e){
    super(msg, e);
  }
}
