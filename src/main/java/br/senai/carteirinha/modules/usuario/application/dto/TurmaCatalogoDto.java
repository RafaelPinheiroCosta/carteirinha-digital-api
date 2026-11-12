package br.senai.carteirinha.modules.usuario.application.dto;
import java.util.List;
public record TurmaCatalogoDto(String codigo, String curso, List<String> unidadesCurriculares) {}
