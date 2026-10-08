package br.senai.carteirinha.modules.professor.application.service;

import br.senai.carteirinha.modules.professor.application.port.in.ListarUnidadesCurricularesProfessorUseCase;
import br.senai.carteirinha.modules.professor.application.port.out.UnidadeCurricularProfessorRepository;
import br.senai.carteirinha.modules.professor.domain.UnidadeCurricularProfessor;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ListarUnidadesCurricularesProfessorService
    implements ListarUnidadesCurricularesProfessorUseCase {

    private final UnidadeCurricularProfessorRepository repository;

    public ListarUnidadesCurricularesProfessorService(
        UnidadeCurricularProfessorRepository repository
    ) {
        this.repository =
            Objects.requireNonNull(
                repository
            );
    }

    @Override
    public List<UnidadeCurricularProfessor> listar(
        UUID professorId
    ) {

        return repository
            .listarUnidadesCurricularesPorProfessor(
                Objects.requireNonNull(
                    professorId
                )
            );
    }
}