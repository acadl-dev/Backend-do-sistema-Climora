package com.acadl.climora.identidade.application;

import com.acadl.climora.compartilhado.RegraDeNegocioException;
import com.acadl.climora.compartilhado.Validacoes;
import com.acadl.climora.identidade.domain.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// A empresa é criada antes da verificação porque é ela que normaliza o documento
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

        String senha = validarSenha(comando.senha());
        Empresa empresa = Empresa.criar(
                comando.nomeEmpresa(), comando.documento(), comando.telefone(), null
        );
        if (empresaRepository.existsByDocumentoNumero(empresa.getDocumento().getNumero())){
            throw new RegraDeNegocioException("Já existe uma conta com este número de documento");
        }
        String email = Validacoes.email(comando.email());
        if (usuarioRepository.existsByEmail(email)){
            throw new RegraDeNegocioException("Já existe uma conta com este email");
        }
        String senhaHash = passwordEncoder.encode(senha);
        Usuario usuario = Usuario.criar(empresa.getId(), comando.nomeUsuario(), email, senhaHash, PerfilUsuario.DONO);
        empresaRepository.save(empresa);
        usuarioRepository.save(usuario);

        return new ContaCadastrada(empresa.getId(), usuario.getId());
    }

    private static String validarSenha(String senha) {
        if (senha == null || senha.length() < 8 || senha.length() > 64) {
            throw new RegraDeNegocioException("A senha deve ter entre 8 e 64 caracteres");
        }
        return senha;
    }
}
