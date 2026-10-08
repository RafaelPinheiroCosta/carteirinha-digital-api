package br.senai.carteirinha.modules.professor.application.dto;

import br.senai.carteirinha.modules.professor.domain.TurmaProfessor;

import java.util.List;

public record TurmaProfessorResponseDto(
    String id,
    String codigo,
    String curso,
    String semestre,
    int quantidadeAlunos,
    List<String> diasSemana,
    String horarioInicio,
    String horarioFim
) {

    public static TurmaProfessorResponseDto from(
        TurmaProfessor turma
    ) {

        return new TurmaProfessorResponseDto(
            turma.id().toString(),
            turma.codigo(),
            turma.curso(),
            turma.semestre(),
            turma.quantidadeAlunos(),
            turma.diasSemana(),
            turma.horarioInicio(),
            turma.horarioFim()
        );
    }
}