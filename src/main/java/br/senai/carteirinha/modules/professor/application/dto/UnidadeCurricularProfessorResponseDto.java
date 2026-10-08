package br.senai.carteirinha.modules.professor.application.dto;

import br.senai.carteirinha.modules.professor.domain.UnidadeCurricularProfessor;

import java.util.List;

public record UnidadeCurricularProfessorResponseDto(
    String id,
    String nome,
    String turmaId,
    String turma,
    int cargaHoraria,
    int quantidadeAulas,
    int aulasRealizadas,
    List<String> diasSemana,
    String horarioInicio,
    String horarioFim
) {

    public static UnidadeCurricularProfessorResponseDto from(
        UnidadeCurricularProfessor unidade
    ) {

        return new UnidadeCurricularProfessorResponseDto(
            unidade.id().toString(),
            unidade.nome(),
            unidade.turmaId().toString(),
            unidade.turma(),
            unidade.cargaHoraria(),
            unidade.quantidadeAulas(),
            unidade.aulasRealizadas(),
            unidade.diasSemana(),
            unidade.horarioInicio(),
            unidade.horarioFim()
        );
    }
}