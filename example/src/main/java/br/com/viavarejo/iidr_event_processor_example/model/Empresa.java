package br.com.viavarejo.iidr_event_processor_example.model;

import br.com.viavarejo.iidr_event_processor.annotations.IIDRAlias;

public class Empresa {
    @IIDRAlias("CD_EMPGCB")
    private int codigo;

    public int getCodigo() {
        return codigo;
    }
}
