package br.senai.carteirinha.modules.usuario.infrastructure.persistence;

import br.senai.carteirinha.modules.usuario.domain.PerfilAcesso;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(
    name = "usuarios",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_usuario_login",
            columnNames = "login"
        ),
        @UniqueConstraint(
            name = "uk_usuario_matricula",
            columnNames = "matricula"
        )
    }
)
public class UsuarioJpaEntity {

    @Id
    private UUID id;

    @Column(
        nullable = false,
        length = 80
    )
    private String login;

    @Column(
        name = "senha_hash",
        nullable = false
    )
    private String senhaHash;

    @Column(
        nullable = false,
        length = 120
    )
    private String nome;

    @Column(
        nullable = false,
        length = 30
    )
    private String matricula;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "perfil",
        length = 20
    )
    private PerfilAcesso perfil;

    @Column(
        nullable = false,
        length = 150
    )
    private String curso;

    @Column(
        nullable = false,
        length = 50
    )
    private String turma;

    @Column(nullable = false)
    private boolean ativo;

    protected UsuarioJpaEntity() {
    }

    public UsuarioJpaEntity(
        UUID id,
        String login,
        String senhaHash,
        String nome,
        String matricula,
        PerfilAcesso perfil,
        String curso,
        String turma,
        boolean ativo
    ) {
        this.id = id;
        this.login = login;
        this.senhaHash = senhaHash;
        this.nome = nome;
        this.matricula = matricula;
        this.perfil = perfil;
        this.curso = curso;
        this.turma = turma;
        this.ativo = ativo;
    }

    public UUID getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public String getNome() {
        return nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public PerfilAcesso getPerfil() {
        return perfil;
    }

    public String getCurso() {
        return curso;
    }

    public String getTurma() {
        return turma;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void aprovar(PerfilAcesso perfil, String curso, String turma, String matricula) {
        if (ativo) throw new IllegalStateException("Usuário já ativo");
        this.perfil = perfil;
        this.curso = curso;
        this.turma = turma;
        this.matricula = matricula;
        this.ativo = true;
    }

    public void definirPerfil(
        PerfilAcesso perfil
    ) {
        this.perfil = perfil;
    }
}