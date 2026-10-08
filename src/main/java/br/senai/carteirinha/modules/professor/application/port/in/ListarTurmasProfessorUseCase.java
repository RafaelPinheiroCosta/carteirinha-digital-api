package br.senai.carteirinha.modules.professor.application.port.in;

import br.senai.carteirinha.modules.professor.domain.TurmaProfessor;

import java.util.List;
import java.util.UUID;

public interface ListarTurmasProfessorUseCase {

    List<TurmaProfessor> listar(
        UUID professorId
    );
}