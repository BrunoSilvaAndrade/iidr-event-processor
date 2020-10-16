package br.com.viavarejo.iidr_event_processor.exceptions;

public class UnsuporttedTypeException extends Exception {
    public UnsuporttedTypeException(String msg){
        super(msg);
    }
}
