package com.acadl.climora.identidade.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {

    boolean existsByDocumentoNumero(String numero);
}
