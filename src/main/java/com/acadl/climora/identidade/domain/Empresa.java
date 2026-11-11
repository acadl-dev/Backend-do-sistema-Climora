package com.acadl.climora.identidade.domain;

import com.acadl.climora.compartilhado.Documento;
import com.acadl.climora.compartilhado.Validacoes;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "empresa", schema = "identidade")
public class Empresa {

    @Id
    private UUID id;

    private String nome;

    @Embedded
    private Documento documento;

    private String telefone;

    private String email;

    private String logoStorageKey;

    @Column(updatable = false)
    private Instant criadaEm;

    private Instant atualizadaEm;

    @Version
    private Long version;

    /** Exigido pelo JPA. Não deve ser usado pelo código da aplicação. */
    protected Empresa() {
    }

    private Empresa(String nome, Documento documento, String telefone, String email) {
        this.id = UUID.randomUUID();
        this.nome = Validacoes.obrigatorio(nome, "nome");
        this.documento = documento;
        this.telefone = Validacoes.opcional(telefone);
        this.email = Validacoes.emailOpcional(email);
    }

    /**
     * Cria uma empresa a partir do CPF ou CNPJ informado.
     * O tipo de pessoa é deduzido do documento.
     */
    public static Empresa criar(String nome, String documento, String telefone, String email) {
        return new Empresa(nome, Documento.de(documento), telefone, email);
    }

    public void alterarNome(String novoNome) {
        this.nome = Validacoes.obrigatorio(novoNome, "nome");
    }

    public void alterarContato(String novoTelefone, String novoEmail) {
        this.telefone = Validacoes.opcional(novoTelefone);
        this.email = Validacoes.emailOpcional(novoEmail);
    }

    public void definirLogo(String storageKey) {
        this.logoStorageKey = Validacoes.obrigatorio(storageKey, "logo");
    }

    public void removerLogo() {
        this.logoStorageKey = null;
    }

    @PrePersist
    private void aoCriar() {
        Instant agora = Instant.now();
        this.criadaEm = agora;
        this.atualizadaEm = agora;
    }

    @PreUpdate
    private void aoAtualizar() {
        this.atualizadaEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Documento getDocumento() {
        return documento;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public String getLogoStorageKey() {
        return logoStorageKey;
    }

    public Instant getCriadaEm() {
        return criadaEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Empresa outra)) return false;
        return id != null && id.equals(outra.id);
    }

    @Override
    public int hashCode() {
        return Empresa.class.hashCode();
    }
}