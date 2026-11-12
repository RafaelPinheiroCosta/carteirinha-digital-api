package br.senai.carteirinha.modules.usuario.application.service;

import br.senai.carteirinha.modules.usuario.application.dto.LoginRequestDto;
import br.senai.carteirinha.modules.usuario.application.dto.LoginResponseDto;
import br.senai.carteirinha.modules.usuario.application.port.in.AutenticarUsuarioUseCase;
import br.senai.carteirinha.modules.usuario.application.port.out.PasswordEncoderPort;
import br.senai.carteirinha.modules.usuario.application.port.out.TokenProviderPort;
import br.senai.carteirinha.modules.usuario.application.port.out.UsuarioRepository;
import br.senai.carteirinha.modules.usuario.domain.CredenciaisInvalidasException;
import br.senai.carteirinha.modules.usuario.domain.Usuario;

public final class AutenticarUsuarioService
    implements AutenticarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public AutenticarUsuarioService(
        UsuarioRepository usuarioRepository,
        PasswordEncoderPort passwordEncoder,
        TokenProviderPort tokenProvider
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public LoginResponseDto autenticar(
        LoginRequestDto credenciais
    ) {

        String login =
            credenciais.login() == null
                ? ""
                : credenciais.login().trim();

        String senha =
            credenciais.senha() == null
                ? ""
                : credenciais.senha();

        if (
            login.isBlank()
                || senha.isBlank()
        ) {
            throw new CredenciaisInvalidasException();
        }

        Usuario usuario =
            usuarioRepository
                .buscarPorLogin(login)
                .orElseThrow(
                    CredenciaisInvalidasException::new
                );

        boolean senhaCorreta =
            passwordEncoder.corresponde(
                senha,
                usuario.senhaHash()
            );

        if (!senhaCorreta) {
            throw new CredenciaisInvalidasException();
        }

        if (!usuario.estaAtivo()) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.FORBIDDEN, "Cadastro aguardando aprovação"
            );
        }

        return new LoginResponseDto(
            usuario.id().toString(),
            usuario.nome(),
            usuario.matricula(),
            usuario.perfil().name(),
            usuario.curso(),
            usuario.turma(),
            tokenProvider.gerarPara(
                usuario
            )
        );
    }
}