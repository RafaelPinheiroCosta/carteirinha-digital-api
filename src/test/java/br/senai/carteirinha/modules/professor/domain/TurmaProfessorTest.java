package br.senai.carteirinha.modules.professor.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TurmaProfessorTest {

    @Test
    void deveCriarTurmaValida() {

        var turma =
            new TurmaProfessor(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "2DEVEST-A",
                "Desenvolvimento de Sistemas",
                "2026.2",
                32,
                List.of(
                    "Segunda-feira",
                    "Quinta-feira"
                ),
                "19:00",
                "22:30"
            );

        assertEquals(
            "2DEVEST-A",
            turma.codigo()
        );

        assertEquals(
            32,
            turma.quantidadeAlunos()
        );
    }

    @Test
    void naoDeveAceitarQuantidadeNegativaDeAlunos() {

        assertThrows(
            IllegalArgumentException.class,
            () ->
                new TurmaProfessor(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    "2DEVEST-A",
                    "Desenvolvimento de Sistemas",
                    "2026.2",
                    -1,
                    List.of(
                        "Segunda-feira"
                    ),
                    "19:00",
                    "22:30"
                )
        );
    }
}