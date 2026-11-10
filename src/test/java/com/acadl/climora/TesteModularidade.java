package com.acadl.climora;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

// Garante que cada modulo acesse os outros corretamente. Caso um tente acessar o outro de forma incorreta esse teste devera impedir
public class TesteModularidade {
    @Test
    void verificaFronteirasDosModulos(){
        ApplicationModules.of(ClimoraBackendApplication.class).verify();
    }
}
