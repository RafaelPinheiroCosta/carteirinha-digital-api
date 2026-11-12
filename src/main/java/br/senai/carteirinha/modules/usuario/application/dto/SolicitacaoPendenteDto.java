package br.senai.carteirinha.modules.usuario.application.dto;
import java.util.UUID;
public record SolicitacaoPendenteDto(UUID id, String nome, String login, String perfilSolicitado, String cursoSolicitado) {}
