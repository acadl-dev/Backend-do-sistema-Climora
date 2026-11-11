package com.acadl.climora.compartilhado;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.Locale;
import java.util.Objects;

/**
 * CPF ou CNPJ válido. O tipo de pessoa é deduzido do próprio número:
 * 11 caracteres é CPF, 14 é CNPJ (numérico ou alfanumérico).
 */
@Embeddable
public class Documento {

    private static final int[] PESOS_CNPJ_DV1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_CNPJ_DV2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa")
    private TipoPessoa tipo;

    @Column(name = "documento")
    private String numero;

    /** Exigido pelo JPA. */
    protected Documento() {
    }

    private Documento(TipoPessoa tipo, String numero) {
        this.tipo = tipo;
        this.numero = numero;
    }

    /** Aceita o número com ou sem pontuação, por exemplo "123.456.789-09" ou "12.ABC.345/01DE-35". */
    public static Documento de(String valor) {
        String numero = normalizar(Validacoes.obrigatorio(valor, "documento"));

        if (numero.length() == 11 && cpfValido(numero)) {
            return new Documento(TipoPessoa.FISICA, numero);
        }
        if (numero.length() == 14 && cnpjValido(numero)) {
            return new Documento(TipoPessoa.JURIDICA, numero);
        }
        throw new RegraDeNegocioException("documento deve ser um CPF ou CNPJ válido");
    }

    private static String normalizar(String valor) {
        return valor.replaceAll("[^0-9A-Za-z]", "").toUpperCase(Locale.ROOT);
    }

    private static boolean cpfValido(String cpf) {
        if (!cpf.matches("\\d{11}") || todosIguais(cpf)) {
            return false;
        }
        int dv1 = digitoCpf(cpf, 9);
        int dv2 = digitoCpf(cpf, 10);
        return dv1 == cpf.charAt(9) - '0' && dv2 == cpf.charAt(10) - '0';
    }

    private static int digitoCpf(String cpf, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (cpf.charAt(i) - '0') * (quantidade + 1 - i);
        }
        return moduloOnze(soma);
    }

    /** Segue a regra da Receita Federal: cada caractere vale seu código ASCII menos 48. */
    private static boolean cnpjValido(String cnpj) {
        if (!cnpj.matches("[0-9A-Z]{12}\\d{2}") || todosIguais(cnpj)) {
            return false;
        }
        int dv1 = digitoCnpj(cnpj, PESOS_CNPJ_DV1);
        int dv2 = digitoCnpj(cnpj, PESOS_CNPJ_DV2);
        return dv1 == cnpj.charAt(12) - '0' && dv2 == cnpj.charAt(13) - '0';
    }

    private static int digitoCnpj(String cnpj, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (cnpj.charAt(i) - '0') * pesos[i];
        }
        return moduloOnze(soma);
    }

    private static int moduloOnze(int soma) {
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static boolean todosIguais(String valor) {
        return valor.chars().allMatch(c -> c == valor.charAt(0));
    }

    public TipoPessoa getTipo() {
        return tipo;
    }

    public String getNumero() {
        return numero;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Documento outro)) return false;
        return Objects.equals(numero, outro.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(numero);
    }
}