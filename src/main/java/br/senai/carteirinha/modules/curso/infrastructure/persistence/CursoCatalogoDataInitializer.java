package br.senai.carteirinha.modules.curso.infrastructure.persistence;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Configuration
public class CursoCatalogoDataInitializer {
    @Bean
    CommandLineRunner carregarCatalogoCursos(
        SpringDataCursoRepository cursos,
        SpringDataUnidadeCurricularCursoRepository unidades
    ) {
        return args -> {
            cadastrarCurso(cursos, unidades, "DS", "Desenvolvimento de Sistemas", List.of(
                "Banco de Dados",
                "Desenvolvimento de Sistemas",
                "Fundamentos de Programação",
                "Programação Back-End",
                "Programação para Dispositivos Móveis",
                "Projeto de Software",
                "Testes de Software"
            ));
            cadastrarCurso(cursos, unidades, "REDES", "Redes de Computadores", List.of(
                "Fundamentos de Redes",
                "Cabeamento Estruturado",
                "Administração de Redes",
                "Segurança de Redes"
            ));
        };
    }

    private void cadastrarCurso(
        SpringDataCursoRepository cursos,
        SpringDataUnidadeCurricularCursoRepository unidades,
        String codigo, String nome, List<String> nomes
    ) {
        if (!cursos.existsById(codigo)) cursos.save(new CursoJpaEntity(codigo, nome));
        for (String uc : nomes) {
            UUID id = UUID.nameUUIDFromBytes(("curso/" + codigo + "/uc/" + uc).getBytes(StandardCharsets.UTF_8));
            if (!unidades.existsById(id)) {
                unidades.save(new UnidadeCurricularCursoJpaEntity(id, codigo, uc));
            }
        }
    }
}
