package br.senai.carteirinha.modules.unidadecurricular.presentation;

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
class UnidadeCurricularControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void deveExigirAutenticacao()
        throws Exception {

        mockMvc
            .perform(
                get(
                    "/unidades-curriculares"
                )
            )
            .andExpect(
                status().isUnauthorized()
            );
    }

    @Test
    void alunoDeveListarTodasAsUcsDoCurso()
        throws Exception {

        String token =
            login(
                "aluno",
                "123"
            );

        mockMvc
            .perform(
                get(
                    "/unidades-curriculares"
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
                    hasSize(7)
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
                jsonPath("$[0].professor")
                    .exists()
            )
            .andExpect(
                jsonPath("$[0].nota1")
                    .exists()
            )
            .andExpect(
                jsonPath("$[0].nota2")
                    .exists()
            )
            .andExpect(
                jsonPath("$[0].media")
                    .exists()
            )
            .andExpect(
                jsonPath("$[0].faltas")
                    .exists()
            );
    }

    @Test
    void professorNaoDeveAcessarEndpointDeUcsDoAluno()
        throws Exception {

        String token =
            login(
                "professor",
                "123"
            );

        mockMvc
            .perform(
                get(
                    "/unidades-curriculares"
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
                    post("/auth/login")
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