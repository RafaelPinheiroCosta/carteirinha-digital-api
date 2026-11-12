package br.senai.carteirinha.modules.curso.infrastructure.persistence;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "curso_unidades_curriculares",
    uniqueConstraints = @UniqueConstraint(name = "uk_uc_curso_nome", columnNames = {"curso_codigo", "nome"}),
    indexes = @Index(name = "idx_uc_curso_codigo", columnList = "curso_codigo"))
public class UnidadeCurricularCursoJpaEntity {
    @Id
    private UUID id;

    @Column(name = "curso_codigo", nullable = false, length = 50)
    private String cursoCodigo;

    @Column(nullable = false, length = 150)
    private String nome;

    protected UnidadeCurricularCursoJpaEntity() {}

    public UnidadeCurricularCursoJpaEntity(UUID id, String cursoCodigo, String nome) {
        this.id = id;
        this.cursoCodigo = cursoCodigo;
        this.nome = nome;
    }

    public UUID getId() { return id; }
    public String getCursoCodigo() { return cursoCodigo; }
    public String getNome() { return nome; }
}
