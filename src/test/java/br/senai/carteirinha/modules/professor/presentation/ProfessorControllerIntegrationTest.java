package br.senai.carteirinha.modules.professor.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProfessorControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void deveExigirAutenticacaoNasTurmas()
        throws Exception {

        mockMvc
            .perform(
                get(
                    "/professores/me/turmas"
                )
            )
            .andExpect(
                status().isUnauthorized()
            );
    }

    @Test
    void alunoNaoDeveAcessarTurmasDoProfessor()
        throws Exception {

        String token =
            login(
                "aluno",
                "123"
            );

        mockMvc
            .perform(
                get(
                    "/professores/me/turmas"
                )
                    .header(
                        "Authorization",
                        "Bearer " + token
                    )
            )
            .andExpect(
                status().isForbidden()
            );
    }

    @Test
    void professorDeveListarSuasTurmas()
        throws Exception {

        String token =
            login(
                "professor",
                "123"
            );

        mockMvc
            .perform(
                get(
                    "/professores/me/turmas"
                )
                    .header(
                        "Authorization",
                        "Bearer " + token
                    )
            )
            .andExpect(
                status().isOk()
            )
            .andExpect(
                jsonPath(
                    "$",
                    hasSize(3)
                )
            )
            .andExpect(
                jsonPath("$[0].id")
                    .exists()
            )
            .andExpect(
                jsonPath("$[0].codigo")
                    .exists()
            )
            .andExpect(
                jsonPath("$[0].curso")
                    .value(
                        "Desenvolvimento de Sistemas"
                    )
            )
            .andExpect(
                jsonPath("$[0].semestre")
                    .value(
                        "2026.2"
                    )
            )
            .andExpect(
                jsonPath("$[0].quantidadeAlunos")
                    .exists()
            )
            .andExpect(
                jsonPath("$[0].diasSemana")
                    .isArray()
            )
            .andExpect(
                jsonPath("$[0].horarioInicio")
                    .exists()
            )
            .andExpect(
                jsonPath("$[0].horarioFim")
                    .exists()
            );
    }

    @Test
    void professorDeveListarSuasUnidadesCurriculares()
        throws Exception {

        String token =
            login(
                "professor",
                "123"
            );

        mockMvc
            .perform(
                get(
                    "/professores/me/unidades-curriculares"
                )
                    .header(
                        "Authorization",
                        "Bearer " + token
                    )
            )
            .andExpect(
                status().isOk()
            )
            .andExpect(
                jsonPath(
                    "$",
                    hasSize(6)
                )
            )
            .andExpect(
                jsonPath("$[0].id")
                    .exists()
            )
            .andExpect(
                jsonPath("$[0].nome")
                    .exists()
            )
            .andExpect(
                jsonPath("$[0].turmaId")
                    .exists()
            )
            .andExpect(
                jsonPath("$[0].turma")
                    .exists()
            )
            .andExpect(
                jsonPath("$[0].cargaHoraria")
                    .exists()
            )
            .andExpect(
                jsonPath("$[0].quantidadeAulas")
                    .exists()
            )
            .andExpect(
                jsonPath("$[0].aulasRealizadas")
                    .exists()
            )
            .andExpect(
                jsonPath("$[0].diasSemana")
                    .isArray()
            )
            .andExpect(
                jsonPath("$[0].horarioInicio")
                    .exists()
            )
            .andExpect(
                jsonPath("$[0].horarioFim")
                    .exists()
            );
    }

    @Test
    void alunoNaoDeveAcessarUcsDoProfessor()
        throws Exception {

        String token =
            login(
                "aluno",
                "123"
            );

        mockMvc
            .perform(
                get(
                    "/professores/me/unidades-curriculares"
                )
                    .header(
                        "Authorization",
                        "Bearer " + token
                    )
            )
            .andExpect(
                status().isForbidden()
            );
    }

    private String login(
        String login,
        String senha
    ) throws Exception {

        String response =
            mockMvc
                .perform(
                    post(
                        "/auth/login"
                    )
                        .contentType(
                            MediaType.APPLICATION_JSON
                        )
                        .content(
                            """
                            {
                              "login": "%s",
                              "senha": "%s"
                            }
                            """.formatted(
                                login,
                                senha
                            )
                        )
                )
                .andExpect(
                    status().isOk()
                )
                .andReturn()
                .getResponse()
                .getContentAsString(
                    StandardCharsets.UTF_8
                );

        return objectMapper
            .readTree(
                response
            )
            .get(
                "token"
            )
            .asText();
    }
}