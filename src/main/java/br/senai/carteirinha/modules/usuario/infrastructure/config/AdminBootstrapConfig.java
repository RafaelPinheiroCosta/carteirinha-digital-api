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
            if (repository.findByLoginIgnoreCase(login).isEmpty()) {
                UUID id = UUID.randomUUID();
                repository.save(new UsuarioJpaEntity(id, login, encoder.encode(password),
                    "Administrador", "ADMIN-" + id, PerfilAcesso.ADMIN, "", "", true));
            }
        };
    }
}
