package br.senai.carteirinha.modules.professor.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(
    name = "professor_unidades_curriculares",
    indexes = {
        @Index(
            name = "idx_professor_uc_professor",
            columnList = "professor_id"
        ),
        @Index(
            name = "idx_professor_uc_turma",
            columnList = "turma_id"
        )
    }
)
public class UnidadeCurricularProfessorJpaEntity {

    @Id
    private UUID id;

    @Column(
        name = "professor_id",
        nullable = false
    )
    private UUID professorId;

    @Column(
        name = "turma_id",
        nullable = false
    )
    private UUID turmaId;

    @Column(
        nullable = false,
        length = 50
    )
    private String turma;

    @Column(
        nullable = false,
        length = 150
    )
    private String nome;

    @Column(
        name = "carga_horaria",
        nullable = false
    )
    private int cargaHoraria;

    @Column(
        name = "quantidade_aulas",
        nullable = false
    )
    private int quantidadeAulas;

    @Column(
        name = "aulas_realizadas",
        nullable = false
    )
    private int aulasRealizadas;

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

    protected UnidadeCurricularProfessorJpaEntity() {
    }

    public UnidadeCurricularProfessorJpaEntity(
        UUID id,
        UUID professorId,
        UUID turmaId,
        String turma,
        String nome,
        int cargaHoraria,
        int quantidadeAulas,
        int aulasRealizadas,
        String diasSemana,
        String horarioInicio,
        String horarioFim
    ) {
        this.id = id;
        this.professorId = professorId;
        this.turmaId = turmaId;
        this.turma = turma;
        this.nome = nome;
        this.cargaHoraria = cargaHoraria;
        this.quantidadeAulas = quantidadeAulas;
        this.aulasRealizadas = aulasRealizadas;
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

    public UUID getTurmaId() {
        return turmaId;
    }

    public String getTurma() {
        return turma;
    }

    public String getNome() {
        return nome;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public int getQuantidadeAulas() {
        return quantidadeAulas;
    }

    public int getAulasRealizadas() {
        return aulasRealizadas;
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