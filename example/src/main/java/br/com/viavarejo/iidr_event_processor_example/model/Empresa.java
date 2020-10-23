package br.com.viavarejo.iidr_event_processor_example.model;

import br.com.viavarejo.iidr_event_processor.annotations.Alias;

public class Empresa {
    @Alias("CD_EMPGCB")
    private int codigo;

    public int getCodigo() {
        return codigo;
    }
}
