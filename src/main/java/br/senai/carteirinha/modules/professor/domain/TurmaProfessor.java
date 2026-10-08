package br.senai.carteirinha.modules.professor.domain;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class TurmaProfessor {

    private final UUID id;
    private final UUID professorId;
    private final String codigo;
    private final String curso;
    private final String semestre;
    private final int quantidadeAlunos;
    private final List<String> diasSemana;
    private final String horarioInicio;
    private final String horarioFim;

    public TurmaProfessor(
        UUID id,
        UUID professorId,
        String codigo,
        String curso,
        String semestre,
        int quantidadeAlunos,
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

        this.codigo =
            exigirTexto(
                codigo,
                "codigo"
            );

        this.curso =
            exigirTexto(
                curso,
                "curso"
            );

        this.semestre =
            exigirTexto(
                semestre,
                "semestre"
            );

        if (quantidadeAlunos < 0) {
            throw new IllegalArgumentException(
                "quantidadeAlunos não pode ser negativa"
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

        this.quantidadeAlunos =
            quantidadeAlunos;

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

    public String codigo() {
        return codigo;
    }

    public String curso() {
        return curso;
    }

    public String semestre() {
        return semestre;
    }

    public int quantidadeAlunos() {
        return quantidadeAlunos;
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