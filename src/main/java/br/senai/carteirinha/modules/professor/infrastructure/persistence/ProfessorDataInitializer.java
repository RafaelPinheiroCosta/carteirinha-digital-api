package br.senai.carteirinha.modules.professor.infrastructure.persistence;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;

@Configuration
public class ProfessorDataInitializer {

    private static final UUID PROFESSOR_ID =
        UUID.fromString(
            "00000000-0000-0000-0000-000000000003"
        );

    private static final UUID TURMA_2DEVEST_A =
        UUID.fromString(
            "30000000-0000-0000-0000-000000000001"
        );

    private static final UUID TURMA_2DEVEST_B =
        UUID.fromString(
            "30000000-0000-0000-0000-000000000002"
        );

    private static final UUID TURMA_1DEVEST_A =
        UUID.fromString(
            "30000000-0000-0000-0000-000000000003"
        );

    @Bean
    CommandLineRunner carregarDadosAcademicosProfessor(
        SpringDataTurmaProfessorRepository turmaRepository,
        SpringDataUnidadeCurricularProfessorRepository unidadeRepository
    ) {

        return args -> {

            garantirTurma(
                turmaRepository,
                new TurmaProfessorJpaEntity(
                    TURMA_2DEVEST_A,
                    PROFESSOR_ID,
                    "2DEVEST-A",
                    "Desenvolvimento de Sistemas",
                    "2026.2",
                    32,
                    "Segunda-feira|Quinta-feira",
                    "19:00",
                    "22:30"
                )
            );

            garantirTurma(
                turmaRepository,
                new TurmaProfessorJpaEntity(
                    TURMA_2DEVEST_B,
                    PROFESSOR_ID,
                    "2DEVEST-B",
                    "Desenvolvimento de Sistemas",
                    "2026.2",
                    29,
                    "Terça-feira|Quarta-feira",
                    "19:00",
                    "22:30"
                )
            );

            garantirTurma(
                turmaRepository,
                new TurmaProfessorJpaEntity(
                    TURMA_1DEVEST_A,
                    PROFESSOR_ID,
                    "1DEVEST-A",
                    "Desenvolvimento de Sistemas",
                    "2026.2",
                    35,
                    "Sexta-feira",
                    "19:00",
                    "22:30"
                )
            );

            List<UnidadeCurricularProfessorJpaEntity>
                unidades =
                    List.of(

                        new UnidadeCurricularProfessorJpaEntity(
                            UUID.fromString(
                                "40000000-0000-0000-0000-000000000001"
                            ),
                            PROFESSOR_ID,
                            TURMA_2DEVEST_A,
                            "2DEVEST-A",
                            "Programação Back-End",
                            75,
                            100,
                            44,
                            "Segunda-feira",
                            "19:00",
                            "22:30"
                        ),

                        new UnidadeCurricularProfessorJpaEntity(
                            UUID.fromString(
                                "40000000-0000-0000-0000-000000000002"
                            ),
                            PROFESSOR_ID,
                            TURMA_2DEVEST_A,
                            "2DEVEST-A",
                            "Programação para Dispositivos Móveis",
                            75,
                            100,
                            36,
                            "Quinta-feira",
                            "19:00",
                            "22:30"
                        ),

                        new UnidadeCurricularProfessorJpaEntity(
                            UUID.fromString(
                                "40000000-0000-0000-0000-000000000003"
                            ),
                            PROFESSOR_ID,
                            TURMA_2DEVEST_B,
                            "2DEVEST-B",
                            "Programação Back-End",
                            75,
                            100,
                            48,
                            "Terça-feira",
                            "19:00",
                            "22:30"
                        ),

                        new UnidadeCurricularProfessorJpaEntity(
                            UUID.fromString(
                                "40000000-0000-0000-0000-000000000004"
                            ),
                            PROFESSOR_ID,
                            TURMA_2DEVEST_B,
                            "2DEVEST-B",
                            "Programação para Dispositivos Móveis",
                            75,
                            100,
                            40,
                            "Quarta-feira",
                            "19:00",
                            "22:30"
                        ),

                        new UnidadeCurricularProfessorJpaEntity(
                            UUID.fromString(
                                "40000000-0000-0000-0000-000000000005"
                            ),
                            PROFESSOR_ID,
                            TURMA_1DEVEST_A,
                            "1DEVEST-A",
                            "Projeto de Software",
                            45,
                            60,
                            21,
                            "Sexta-feira",
                            "19:00",
                            "21:15"
                        ),

                        new UnidadeCurricularProfessorJpaEntity(
                            UUID.fromString(
                                "40000000-0000-0000-0000-000000000006"
                            ),
                            PROFESSOR_ID,
                            TURMA_1DEVEST_A,
                            "1DEVEST-A",
                            "Fundamentos de Programação",
                            60,
                            80,
                            30,
                            "Sexta-feira",
                            "21:15",
                            "22:30"
                        )
                    );

            for (
                UnidadeCurricularProfessorJpaEntity unidade :
                    unidades
            ) {

                garantirUnidade(
                    unidadeRepository,
                    unidade
                );
            }
        };
    }

    private void garantirTurma(
        SpringDataTurmaProfessorRepository repository,
        TurmaProfessorJpaEntity turma
    ) {

        if (
            !repository.existsById(
                turma.getId()
            )
        ) {

            repository.save(
                turma
            );
        }
    }

    private void garantirUnidade(
        SpringDataUnidadeCurricularProfessorRepository repository,
        UnidadeCurricularProfessorJpaEntity unidade
    ) {

        if (
            !repository.existsById(
                unidade.getId()
            )
        ) {

            repository.save(
                unidade
            );
        }
    }
}