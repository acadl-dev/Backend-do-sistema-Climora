package com.acadl.climora.identidade.infrastructure.web;

/*
O record gera o toString automaticamente para senha. O que pode expor a senha em algum log.
Pensar em como o record permite mudar esse comportamento padrão. Lembrar que o CadastrarContaComando
também carrega a senha e tem o mesmo problema.
*/

public record CadastroRequestDTO(String nomeEmpresa,
                                 String documento,
                                 String telefone,
                                 String nomeUsuario,
                                 String email,
                                 String senha) {

}
