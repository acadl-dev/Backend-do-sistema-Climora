package com.acadl.climora.identidade.application;

import com.acadl.climora.identidade.domain.EmpresaRepository;
import com.acadl.climora.identidade.domain.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastrarConta {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CadastrarConta(EmpresaRepository empresaRepository,
                          UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ContaCadastrada executar(CadastrarContaComando comando) {
        // TODO:
        // 1. Validar a senha em texto puro
        // 2. Criar a Empresa
        // 3. Recusar se o documento já estiver em uso
        // 4. Recusar se o e-mail já estiver em uso
        // 5. Criptografar a senha
        // 6. Criar o Usuario com perfil DONO
        // 7. Salvar os dois
        // 8. Devolver os ids

        return
    }
}
