package br.senai.carteirinha.modules.professor.application.port.out;

import br.senai.carteirinha.modules.professor.domain.UnidadeCurricularProfessor;

import java.util.List;
import java.util.UUID;

public interface UnidadeCurricularProfessorRepository {

    List<UnidadeCurricularProfessor>
        listarUnidadesCurricularesPorProfessor(
            UUID professorId
        );
}