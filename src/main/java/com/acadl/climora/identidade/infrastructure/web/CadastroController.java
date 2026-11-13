package com.acadl.climora.identidade.infrastructure.web;

import com.acadl.climora.identidade.application.CadastrarConta;
import com.acadl.climora.identidade.application.CadastrarContaComando;
import com.acadl.climora.identidade.application.ContaCadastrada;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class CadastroController {

    private final CadastrarConta cadastrarConta;

    public CadastroController(CadastrarConta cadastrarConta) {
        this.cadastrarConta = cadastrarConta;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<CadastroResponseDTO> cadastrar(@RequestBody CadastroRequestDTO request) {
        CadastrarContaComando comando = new CadastrarContaComando(
                request.nomeEmpresa(),
                request.documento(),
                request.telefone(),
                request.nomeUsuario(),
                request.email(),
                request.senha()
        );

        ContaCadastrada conta = cadastrarConta.executar(comando);

        CadastroResponseDTO response = new CadastroResponseDTO(conta.usuarioId(), conta.empresaId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}