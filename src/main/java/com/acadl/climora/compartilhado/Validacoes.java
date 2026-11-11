package com.acadl.climora.compartilhado;

import java.util.Locale;

/** Validações de entrada reutilizadas pelos modelos de domínio de todos os módulos. */
public final class Validacoes {

    private Validacoes() {
    }

    /** Exige um texto preenchido e devolve o valor sem espaços nas pontas. */
    public static String obrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new RegraDeNegocioException(campo + " é obrigatório");
        }
        return valor.trim();
    }

    /** Devolve o texto sem espaços nas pontas, ou null se não foi informado. */
    public static String opcional(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }

    /** Exige um e-mail válido e devolve em minúsculas. */
    public static String email(String valor) {
        String email = obrigatorio(valor, "email").toLowerCase(Locale.ROOT);
        if (!email.contains("@")) {
            throw new RegraDeNegocioException("email inválido");
        }
        return email;
    }

    /** Valida o e-mail apenas se ele foi informado. */
    public static String emailOpcional(String valor) {
        return opcional(valor) == null ? null : email(valor);
    }
}