package br.senai.carteirinha.modules.usuario.infrastructure.config;
import br.senai.carteirinha.modules.usuario.domain.PerfilAcesso;
import br.senai.carteirinha.modules.usuario.infrastructure.persistence.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;
@Configuration
public class AdminBootstrapConfig {
    @Bean
    CommandLineRunner criarAdministradorSomenteComCredenciaisConfiguradas(
        SpringDataUsuarioRepository repository, PasswordEncoder encoder,
        @Value("${app.bootstrap.admin-login:}") String login,
        @Value("${app.bootstrap.admin-password:}") String password
    ) {
        return args -> {
            if (login.isBlank() && password.isBlank()) return;
            if (login.isBlank() || password.length() < 12)
                throw new IllegalStateException("Configure login e senha do administrador (mínimo 12 caracteres)");
            var existente = repository.findByLoginIgnoreCase(login);
            if (existente.isPresent() && existente.get().getPerfil() != PerfilAcesso.ADMIN)
                throw new IllegalStateException("Login do administrador já pertence a outro perfil");
            if (existente.isEmpty()) {
                UUID id = UUID.randomUUID();
                repository.save(new UsuarioJpaEntity(id, login, encoder.encode(password),
                    "Administrador", "ADMIN-" + id.toString().replace("-", "").substring(0, 20), PerfilAcesso.ADMIN, "", "", true));
            }
        };
    }
}
