package br.senai.carteirinha.modules.professor.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(
    name = "professor_turmas",
    indexes = {
        @Index(
            name = "idx_professor_turma_professor",
            columnList = "professor_id"
        )
    }
)
public class TurmaProfessorJpaEntity {

    @Id
    private UUID id;

    @Column(
        name = "professor_id",
        nullable = false
    )
    private UUID professorId;

    @Column(
        nullable = false,
        length = 50
    )
    private String codigo;

    @Column(
        nullable = false,
        length = 150
    )
    private String curso;

    @Column(
        nullable = false,
        length = 30
    )
    private String semestre;

    @Column(
        name = "quantidade_alunos",
        nullable = false
    )
    private int quantidadeAlunos;

    @Column(
        name = "dias_semana",
        nullable = false,
        length = 150
    )
    private String diasSemana;

    @Column(
        name = "horario_inicio",
        nullable = false,
        length = 10
    )
    private String horarioInicio;

    @Column(
        name = "horario_fim",
        nullable = false,
        length = 10
    )
    private String horarioFim;

    protected TurmaProfessorJpaEntity() {
    }

    public TurmaProfessorJpaEntity(
        UUID id,
        UUID professorId,
        String codigo,
        String curso,
        String semestre,
        int quantidadeAlunos,
        String diasSemana,
        String horarioInicio,
        String horarioFim
    ) {
        this.id = id;
        this.professorId = professorId;
        this.codigo = codigo;
        this.curso = curso;
        this.semestre = semestre;
        this.quantidadeAlunos = quantidadeAlunos;
        this.diasSemana = diasSemana;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProfessorId() {
        return professorId;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCurso() {
        return curso;
    }

    public String getSemestre() {
        return semestre;
    }

    public int getQuantidadeAlunos() {
        return quantidadeAlunos;
    }

    public String getDiasSemana() {
        return diasSemana;
    }

    public String getHorarioInicio() {
        return horarioInicio;
    }

    public String getHorarioFim() {
        return horarioFim;
    }
}