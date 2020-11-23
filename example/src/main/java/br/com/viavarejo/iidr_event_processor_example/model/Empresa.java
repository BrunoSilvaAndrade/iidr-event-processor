package br.com.viavarejo.iidr_event_processor_example.model;

import br.com.viavarejo.iidr_event_processor.annotations.IIDRAlias;
import br.com.viavarejo.iidr_event_processor.annotations.IIDRNonNull;

public class Empresa {
    @IIDRNonNull
    @IIDRAlias("CD_EMPGCB")
    private int codigo;

    public int getCodigo() {
        return codigo;
    }
}
