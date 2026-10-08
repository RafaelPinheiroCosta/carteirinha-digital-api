package br.senai.carteirinha.modules.professor.domain;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class UnidadeCurricularProfessor {

    private final UUID id;
    private final UUID professorId;
    private final UUID turmaId;
    private final String turma;
    private final String nome;
    private final int cargaHoraria;
    private final int quantidadeAulas;
    private final int aulasRealizadas;
    private final List<String> diasSemana;
    private final String horarioInicio;
    private final String horarioFim;

    public UnidadeCurricularProfessor(
        UUID id,
        UUID professorId,
        UUID turmaId,
        String turma,
        String nome,
        int cargaHoraria,
        int quantidadeAulas,
        int aulasRealizadas,
        List<String> diasSemana,
        String horarioInicio,
        String horarioFim
    ) {
        this.id =
            Objects.requireNonNull(
                id,
                "id não pode ser nulo"
            );

        this.professorId =
            Objects.requireNonNull(
                professorId,
                "professorId não pode ser nulo"
            );

        this.turmaId =
            Objects.requireNonNull(
                turmaId,
                "turmaId não pode ser nulo"
            );

        this.turma =
            exigirTexto(
                turma,
                "turma"
            );

        this.nome =
            exigirTexto(
                nome,
                "nome"
            );

        if (cargaHoraria <= 0) {
            throw new IllegalArgumentException(
                "cargaHoraria deve ser maior que zero"
            );
        }

        if (quantidadeAulas <= 0) {
            throw new IllegalArgumentException(
                "quantidadeAulas deve ser maior que zero"
            );
        }

        if (
            aulasRealizadas < 0
                || aulasRealizadas > quantidadeAulas
        ) {
            throw new IllegalArgumentException(
                "aulasRealizadas deve estar entre zero e quantidadeAulas"
            );
        }

        if (
            diasSemana == null
                || diasSemana.isEmpty()
        ) {
            throw new IllegalArgumentException(
                "diasSemana não pode estar vazio"
            );
        }

        this.cargaHoraria =
            cargaHoraria;

        this.quantidadeAulas =
            quantidadeAulas;

        this.aulasRealizadas =
            aulasRealizadas;

        this.diasSemana =
            List.copyOf(
                diasSemana
            );

        this.horarioInicio =
            exigirTexto(
                horarioInicio,
                "horarioInicio"
            );

        this.horarioFim =
            exigirTexto(
                horarioFim,
                "horarioFim"
            );
    }

    public UUID id() {
        return id;
    }

    public UUID professorId() {
        return professorId;
    }

    public UUID turmaId() {
        return turmaId;
    }

    public String turma() {
        return turma;
    }

    public String nome() {
        return nome;
    }

    public int cargaHoraria() {
        return cargaHoraria;
    }

    public int quantidadeAulas() {
        return quantidadeAulas;
    }

    public int aulasRealizadas() {
        return aulasRealizadas;
    }

    public List<String> diasSemana() {
        return diasSemana;
    }

    public String horarioInicio() {
        return horarioInicio;
    }

    public String horarioFim() {
        return horarioFim;
    }

    private static String exigirTexto(
        String valor,
        String campo
    ) {

        if (
            valor == null
                || valor.isBlank()
        ) {
            throw new IllegalArgumentException(
                campo + " não pode estar vazio"
            );
        }

        return valor.trim();
    }
}