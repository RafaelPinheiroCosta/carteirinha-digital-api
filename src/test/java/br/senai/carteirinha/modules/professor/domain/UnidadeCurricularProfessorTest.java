package br.senai.carteirinha.modules.professor.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnidadeCurricularProfessorTest {

    @Test
    void deveCriarOfertaValida() {

        var unidade =
            new UnidadeCurricularProfessor(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "2DEVEST-A",
                "Programação Back-End",
                75,
                100,
                44,
                List.of(
                    "Segunda-feira"
                ),
                "19:00",
                "22:30"
            );

        assertEquals(
            44,
            unidade.aulasRealizadas()
        );

        assertEquals(
            100,
            unidade.quantidadeAulas()
        );
    }

    @Test
    void naoDeveAceitarMaisAulasRealizadasQueTotal() {

        assertThrows(
            IllegalArgumentException.class,
            () ->
                new UnidadeCurricularProfessor(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    "2DEVEST-A",
                    "Programação Back-End",
                    75,
                    100,
                    101,
                    List.of(
                        "Segunda-feira"
                    ),
                    "19:00",
                    "22:30"
                )
        );
    }
}