package br.senai.carteirinha.modules.professor.application.port.in;

import br.senai.carteirinha.modules.professor.domain.UnidadeCurricularProfessor;

import java.util.List;
import java.util.UUID;

public interface ListarUnidadesCurricularesProfessorUseCase {

    List<UnidadeCurricularProfessor> listar(
        UUID professorId
    );
}