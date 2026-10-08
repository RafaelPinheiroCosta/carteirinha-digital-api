package br.senai.carteirinha.modules.professor.application.port.out;

import br.senai.carteirinha.modules.professor.domain.TurmaProfessor;

import java.util.List;
import java.util.UUID;

public interface TurmaProfessorRepository {

    List<TurmaProfessor> listarTurmasPorProfessor(
        UUID professorId
    );
}