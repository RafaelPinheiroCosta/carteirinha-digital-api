package br.senai.carteirinha.modules.usuario.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Entidade de domínio do usuário.
 *
 * Não conhece Spring, JPA, HTTP, JWT ou BCrypt.
 */
public final class Usuario {

    private final UUID id;
    private final String login;
    private final String senhaHash;
    private final String nome;
    private final String matricula;
    private final PerfilAcesso perfil;
    private final String curso;
    private final String turma;
    private final boolean ativo;

    public Usuario(
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
        this.id =
            Objects.requireNonNull(
                id,
                "id não pode ser nulo"
            );

        this.login =
            exigirTexto(
                login,
                "login"
            );

        this.senhaHash =
            exigirTexto(
                senhaHash,
                "senhaHash"
            );

        this.nome =
            exigirTexto(
                nome,
                "nome"
            );

        this.matricula =
            exigirTexto(
                matricula,
                "matricula"
            );

        this.perfil =
            Objects.requireNonNull(
                perfil,
                "perfil não pode ser nulo"
            );

        if (perfil == PerfilAcesso.ALUNO) {

            this.curso =
                exigirTexto(
                    curso,
                    "curso"
                );

            this.turma = ativo ? exigirTexto(turma, "turma") : textoOpcional(turma);

        } else {

            this.curso =
                textoOpcional(
                    curso
                );

            this.turma =
                textoOpcional(
                    turma
                );
        }

        this.ativo = ativo;
    }

    public UUID id() {
        return id;
    }

    public String login() {
        return login;
    }

    public String senhaHash() {
        return senhaHash;
    }

    public String nome() {
        return nome;
    }

    public String matricula() {
        return matricula;
    }

    public PerfilAcesso perfil() {
        return perfil;
    }

    public String curso() {
        return curso;
    }

    public String turma() {
        return turma;
    }

    public boolean estaAtivo() {
        return ativo;
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

    private static String textoOpcional(
        String valor
    ) {

        if (valor == null) {
            return "";
        }

        return valor.trim();
    }
}