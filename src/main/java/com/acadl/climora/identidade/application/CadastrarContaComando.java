package com.acadl.climora.identidade.application;

public record CadastrarContaComando(
        String nomeEmpresa,
        String documento,
        String telefone,
        String nomeUsuario,
        String email,
        String senha
) {}
