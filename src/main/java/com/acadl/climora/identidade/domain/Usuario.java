package com.acadl.climora.identidade.domain;

import com.acadl.climora.compartilhado.RegraDeNegocioException;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name="usuario", schema="identidade")
public class Usuario {

    private @Id UUID id;
    private @Column(nullable = false, updatable = false) UUID empresaId;
    private String nome;
    private String email;
    private String senhaHash;
    private @Enumerated(EnumType.STRING) PerfilUsuario perfil;
    private boolean ativo;
    private Instant ultimoAcessoEm;
    private @Column(updatable = false) Instant criadoEm;
    private Instant atualizadoEm;
    private @Version Long version;

    /** Exigido pelo JPA. Não deve ser usado pelo código da aplicação. */
    protected Usuario() {
    }

    private Usuario(UUID empresaId, String nome, String email, String senhaHash, PerfilUsuario perfil) {
        this.id = UUID.randomUUID();
        this.empresaId = Objects.requireNonNull(empresaId, "empresaId é obrigatório");
        this.nome = obrigatorio(nome, "nome");
        this.email = normalizarEmail(email);
        this.senhaHash = obrigatorio(senhaHash, "senhaHash");
        this.perfil = Objects.requireNonNull(perfil, "perfil é obrigatório");
        this.ativo = true;
    }

    /**
     * Cria um novo usuário ativo.
     * A senha deve chegar já criptografada: o domínio não conhece o algoritmo de hash.
     */
    public static Usuario criar(UUID empresaId, String nome, String email, String senhaHash, PerfilUsuario perfil) {
        return new Usuario(empresaId, nome, email, senhaHash, perfil);
    }

    public void alterarNome(String novoNome) {
        this.nome = obrigatorio(novoNome, "nome");
    }

    public void alterarSenha(String novoSenhaHash) {
        this.senhaHash = obrigatorio(novoSenhaHash, "senhaHash");
    }

    public void desativar() {
        if (!ativo) {
            throw new RegraDeNegocioException("Usuário já está inativo");
        }
        this.ativo = false;
    }

    public void reativar() {
        if (ativo) {
            throw new RegraDeNegocioException("Usuário já está ativo");
        }
        this.ativo = true;
    }

    public void registrarAcesso() {
        if (!ativo) {
            throw new RegraDeNegocioException("Usuário inativo não pode acessar o sistema");
        }
        this.ultimoAcessoEm = Instant.now();
    }

    @PrePersist
    private void aoCriar() {
        Instant agora = Instant.now();
        this.criadoEm = agora;
        this.atualizadoEm = agora;
    }

    @PreUpdate
    private void aoAtualizar() {
        this.atualizadoEm = Instant.now();
    }

    private static String obrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new RegraDeNegocioException(campo + " é obrigatório");
        }
        return valor.trim();
    }

    private static String normalizarEmail(String email) {
        String valor = obrigatorio(email, "email").toLowerCase();
        if (!valor.contains("@")) {
            throw new RegraDeNegocioException("email inválido");
        }
        return valor;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEmpresaId() {
        return empresaId;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    /** Usado apenas na autenticação. Nunca deve ser exposto em respostas da API. */
    public String getSenhaHash() {
        return senhaHash;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Instant getUltimoAcessoEm() {
        return ultimoAcessoEm;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario outro)) return false;
        return id != null && id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Usuario.class.hashCode();
    }
}
