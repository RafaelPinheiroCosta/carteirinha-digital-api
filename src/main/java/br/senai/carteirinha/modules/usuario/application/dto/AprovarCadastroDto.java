package br.senai.carteirinha.modules.usuario.application.dto;

import java.util.List;

/**
 * O perfil e o curso do candidato são definidos no cadastro público.
 * O administrador só atribui matrícula/turma ou a carga docente.
 */
public record AprovarCadastroDto(
    String turma,
    String matricula,
    List<AtribuicaoDocenteDto> atribuicoes
) {
    public record AtribuicaoDocenteDto(String turma, String unidadeCurricular) {}
}
