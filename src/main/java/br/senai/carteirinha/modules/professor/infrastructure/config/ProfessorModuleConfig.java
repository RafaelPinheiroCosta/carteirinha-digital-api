package br.senai.carteirinha.modules.professor.infrastructure.config;

import br.senai.carteirinha.modules.professor.application.port.in.ListarTurmasProfessorUseCase;
import br.senai.carteirinha.modules.professor.application.port.in.ListarUnidadesCurricularesProfessorUseCase;
import br.senai.carteirinha.modules.professor.application.port.out.TurmaProfessorRepository;
import br.senai.carteirinha.modules.professor.application.port.out.UnidadeCurricularProfessorRepository;
import br.senai.carteirinha.modules.professor.application.service.ListarTurmasProfessorService;
import br.senai.carteirinha.modules.professor.application.service.ListarUnidadesCurricularesProfessorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfessorModuleConfig {

    @Bean
    ListarTurmasProfessorUseCase
        listarTurmasProfessorUseCase(
            TurmaProfessorRepository repository
        ) {

        return new ListarTurmasProfessorService(
            repository
        );
    }

    @Bean
    ListarUnidadesCurricularesProfessorUseCase
        listarUnidadesCurricularesProfessorUseCase(
            UnidadeCurricularProfessorRepository repository
        ) {

        return new ListarUnidadesCurricularesProfessorService(
            repository
        );
    }
}