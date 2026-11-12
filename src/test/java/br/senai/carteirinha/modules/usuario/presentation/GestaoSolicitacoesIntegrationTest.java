package br.senai.carteirinha.modules.usuario.presentation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "app.bootstrap.admin-login=admin-validacao",
    "app.bootstrap.admin-password=SenhaAdministrativaDeTeste123"
})
@AutoConfigureMockMvc
class GestaoSolicitacoesIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private String tokenAdmin() throws Exception {
        String corpo = mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"login\":\"admin-validacao\",\"senha\":\"SenhaAdministrativaDeTeste123\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return json.readTree(corpo).get("token").asText();
    }

    @Test
    void cadastroAlunoFicaPendenteEAprovacaoDefineTurmaEMatricula() throws Exception {
        String login = "aluno-" + UUID.randomUUID();
        mvc.perform(get("/public/cursos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasItem("Desenvolvimento de Sistemas")));

        mvc.perform(post("/public/cadastros").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(new Object() {
                    public final String nome = "Aluno Teste";
                    public final String senha = "SenhaForte123";
                    public final String perfil = "ALUNO";
                    public final String curso = "Desenvolvimento de Sistemas";
                    public final String usuario = login;
                    public final String loginSolicitado = login;
                    public String getLogin() { return login; }
                })))
            .andExpect(status().isAccepted());

        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(java.util.Map.of("login", login, "senha", "SenhaForte123"))))
            .andExpect(status().isForbidden());

        String token = tokenAdmin();
        String lista = mvc.perform(get("/admin/solicitacoes").param("perfil", "ALUNO")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode pendentes = json.readTree(lista);
        String id = null;
        for (JsonNode item : pendentes) if (login.equals(item.get("login").asText())) id = item.get("id").asText();
        org.junit.jupiter.api.Assertions.assertNotNull(id);

        mvc.perform(put("/admin/solicitacoes/{id}/aprovacao", id)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(java.util.Map.of(
                    "perfil", "ALUNO", "curso", "Desenvolvimento de Sistemas",
                    "turma", "2DEVEST-A", "matricula", "T" + UUID.randomUUID().toString().substring(0, 12)
                ))))
            .andExpect(status().isNoContent());

        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(java.util.Map.of("login", login, "senha", "SenhaForte123"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.perfil").value("ALUNO"))
            .andExpect(jsonPath("$.turma").value("2DEVEST-A"));
    }

    @Test
    void professorPermiteMultiplasAtribuicoesERejeicaoExcluiCadastro() throws Exception {
        String login = "prof-" + UUID.randomUUID();
        mvc.perform(post("/public/cadastros").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(java.util.Map.of("nome", "Professor Teste", "login", login,
                    "senha", "SenhaForte123", "perfil", "PROFESSOR"))))
            .andExpect(status().isAccepted());
        String token = tokenAdmin();
        String lista = mvc.perform(get("/admin/solicitacoes").param("perfil", "PROFESSOR")
                .header("Authorization", "Bearer " + token))
            .andReturn().getResponse().getContentAsString();
        String id = null;
        for (JsonNode item : json.readTree(lista)) if (login.equals(item.get("login").asText())) id = item.get("id").asText();
        org.junit.jupiter.api.Assertions.assertNotNull(id);

        mvc.perform(put("/admin/solicitacoes/{id}/aprovacao", id)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(java.util.Map.of("perfil", "PROFESSOR",
                    "atribuicoes", java.util.List.of(
                        java.util.Map.of("turma", "2DEVEST-A", "unidadeCurricular", "Programação Back-End"),
                        java.util.Map.of("turma", "2DEVEST-B", "unidadeCurricular", "Programação para Dispositivos Móveis")
                    )))))
            .andExpect(status().isNoContent());

        String tokenProfessor = json.readTree(mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(java.util.Map.of("login", login, "senha", "SenhaForte123"))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.perfil").value("PROFESSOR"))
            .andReturn().getResponse().getContentAsString()).get("token").asText();
        mvc.perform(get("/professores/me/turmas").header("Authorization", "Bearer " + tokenProfessor))
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/professores/me/unidades-curriculares").header("Authorization", "Bearer " + tokenProfessor))
            .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));

        String rejeitado = "rejeitado-" + UUID.randomUUID();
        mvc.perform(post("/public/cadastros").contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(java.util.Map.of("nome", "Candidato", "login", rejeitado,
                "senha", "SenhaForte123", "perfil", "PROFESSOR")))).andExpect(status().isAccepted());
        String outros = mvc.perform(get("/admin/solicitacoes").param("perfil", "PROFESSOR")
                .header("Authorization", "Bearer " + token)).andReturn().getResponse().getContentAsString();
        String idRejeitado = null;
        for (JsonNode item : json.readTree(outros)) if (rejeitado.equals(item.get("login").asText()))
            idRejeitado = item.get("id").asText();
        org.junit.jupiter.api.Assertions.assertNotNull(idRejeitado);
        mvc.perform(delete("/admin/solicitacoes/{id}", idRejeitado)
                .header("Authorization", "Bearer " + token)).andExpect(status().isNoContent());
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(java.util.Map.of("login", rejeitado, "senha", "SenhaForte123"))))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void naoPermiteCadastroPublicoComoAdministrador() throws Exception {
        mvc.perform(post("/public/cadastros").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(java.util.Map.of("nome", "Invasor", "login", "invasor-" + UUID.randomUUID(),
                    "senha", "SenhaForte123", "perfil", "ADMIN"))))
            .andExpect(status().isBadRequest());
    }
}
