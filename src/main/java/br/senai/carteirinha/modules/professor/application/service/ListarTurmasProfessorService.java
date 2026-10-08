package br.senai.carteirinha.modules.professor.application.service;

import br.senai.carteirinha.modules.professor.application.port.in.ListarTurmasProfessorUseCase;
import br.senai.carteirinha.modules.professor.application.port.out.TurmaProfessorRepository;
import br.senai.carteirinha.modules.professor.domain.TurmaProfessor;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ListarTurmasProfessorService
    implements ListarTurmasProfessorUseCase {

    private final TurmaProfessorRepository repository;

    public ListarTurmasProfessorService(
        TurmaProfessorRepository repository
    ) {
        this.repository =
            Objects.requireNonNull(
                repository
            );
    }

    @Override
    public List<TurmaProfessor> listar(
        UUID professorId
    ) {

        return repository
            .listarTurmasPorProfessor(
                Objects.requireNonNull(
                    professorId
                )
            );
    }
}