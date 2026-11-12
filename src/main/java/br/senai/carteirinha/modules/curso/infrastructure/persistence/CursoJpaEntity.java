package br.senai.carteirinha.modules.curso.infrastructure.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "cursos", uniqueConstraints = @UniqueConstraint(name = "uk_curso_nome", columnNames = "nome"))
public class CursoJpaEntity {
    @Id
    @Column(length = 50)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nome;

    protected CursoJpaEntity() {}

    public CursoJpaEntity(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
}
