package br.senai.carteirinha.modules.curso.presentation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "app.bootstrap.admin-login=admin-validacao-cursos",
    "app.bootstrap.admin-password=SenhaAdminCursosTeste123"
})
@AutoConfigureMockMvc
class CursoMatriculaIntegracaoTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    private String login(String usuario, String senha) throws Exception {
        String json = mvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(Map.of("login", usuario, "senha", senha))))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(json).path("token").asText();
    }

    @Test
    void cursoRedesGeraGradeSemCriarUcsPorAluno() throws Exception {
        mvc.perform(get("/public/cursos")).andExpect(status().isOk())
            .andExpect(jsonPath("$", hasItems("Desenvolvimento de Sistemas", "Redes de Computadores")));
        String usuario = "aluno-redes-" + UUID.randomUUID();
        mvc.perform(post("/public/cadastros").contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(Map.of(
                "nome", "Aluno Redes", "login", usuario, "senha", "SenhaRedes123",
                "perfil", "ALUNO", "curso", "Redes de Computadores"
            )))).andExpect(status().isAccepted());

        String admin = login("admin-validacao-cursos", "SenhaAdminCursosTeste123");
        String lista = mvc.perform(get("/admin/solicitacoes").param("perfil", "ALUNO")
            .header("Authorization", "Bearer " + admin))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String id = null;
        for (JsonNode candidato : mapper.readTree(lista)) {
            if (usuario.equals(candidato.path("login").asText())) {
                id = candidato.path("id").asText();
            }
        }
        org.junit.jupiter.api.Assertions.assertNotNull(id);

        mvc.perform(put("/admin/solicitacoes/{id}/aprovacao", id)
            .header("Authorization", "Bearer " + admin)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(Map.of(
                "turma", "1REDES-A",
                "matricula", "RED" + UUID.randomUUID().toString().substring(0, 12)
            )))).andExpect(status().isNoContent());

        String jwt = login(usuario, "SenhaRedes123");
        mvc.perform(get("/unidades-curriculares")
            .header("Authorization", "Bearer " + jwt))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4)))
            .andExpect(jsonPath("$[*].nome", hasItems(
                "Fundamentos de Redes", "Cabeamento Estruturado",
                "Administração de Redes", "Segurança de Redes")))
            .andExpect(jsonPath("$[*].professor", everyItem(is("Professor Modelo"))))
            .andExpect(jsonPath("$[*].nome", not(hasItem("Programação Back-End"))));
    }

    @Test
    void gradeDoAlunoOriginalContinuaVinculadaAoSeuCurso() throws Exception {
        String jwt = login("aluno", "123");
        mvc.perform(get("/unidades-curriculares")
            .header("Authorization", "Bearer " + jwt))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(7)))
            .andExpect(jsonPath("$[*].nome", hasItem("Programação Back-End")))
            .andExpect(jsonPath("$[*].nome", not(hasItem("Fundamentos de Redes"))));
    }
}
