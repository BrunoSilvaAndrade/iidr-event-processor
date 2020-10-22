package br.com.viavarejo.iidr_event_processor.exceptions;

public class WrongJsonSyntaxException extends Exception {
  public WrongJsonSyntaxException(Throwable e) {
    super(e);
  }
}
