package br.senai.carteirinha.modules.usuario.application.dto;
import java.util.List;
public record AprovarCadastroDto(String perfil, String curso, String turma, String matricula, List<AtribuicaoDocenteDto> atribuicoes) {
    public record AtribuicaoDocenteDto(String turma, String unidadeCurricular) {}
}
