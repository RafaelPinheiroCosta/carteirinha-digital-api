package br.senai.carteirinha.modules.usuario.application.service;

import br.senai.carteirinha.modules.professor.infrastructure.persistence.*;
import br.senai.carteirinha.modules.usuario.application.dto.*;
import br.senai.carteirinha.modules.usuario.domain.PerfilAcesso;
import br.senai.carteirinha.modules.usuario.infrastructure.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Service
public class GestaoSolicitacoesService {
    private final SpringDataUsuarioRepository usuarios;
    private final SpringDataTurmaProfessorRepository turmas;
    private final SpringDataUnidadeCurricularProfessorRepository unidades;
    private final PasswordEncoder senhas;

    public GestaoSolicitacoesService(SpringDataUsuarioRepository usuarios,
        SpringDataTurmaProfessorRepository turmas,
        SpringDataUnidadeCurricularProfessorRepository unidades,
        PasswordEncoder senhas) {
        this.usuarios = usuarios;
        this.turmas = turmas;
        this.unidades = unidades;
        this.senhas = senhas;
    }

    public List<String> cursos() {
        return turmas.findAll().stream().map(TurmaProfessorJpaEntity::getCurso)
            .filter(s -> s != null && !s.isBlank()).distinct().sorted().toList();
    }

    public List<TurmaCatalogoDto> catalogo() {
        return turmas.findAll().stream().collect(
            java.util.stream.Collectors.toMap(TurmaProfessorJpaEntity::getCodigo,
                turma -> turma, (primeira, outra) -> primeira)
        ).values().stream().sorted(Comparator.comparing(TurmaProfessorJpaEntity::getCodigo))
        .map(t -> new TurmaCatalogoDto(t.getCodigo(), t.getCurso(),
            unidades.findAll().stream().filter(u -> u.getTurma().equals(t.getCodigo()))
              .map(UnidadeCurricularProfessorJpaEntity::getNome).distinct().sorted().toList()))
        .toList();
    }

    private static String obrigatorio(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, campo + " é obrigatório");
        }
        return texto.trim();
    }

    private PerfilAcesso perfilPublico(String texto) {
        try {
            PerfilAcesso perfil = PerfilAcesso.valueOf(obrigatorio(texto, "perfil").toUpperCase(Locale.ROOT));
            if (perfil == PerfilAcesso.ADMIN) throw new IllegalArgumentException();
            return perfil;
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Perfil deve ser ALUNO ou PROFESSOR");
        }
    }

    @Transactional
    public void cadastrar(SolicitacaoCadastroDto dto) {
        String nome = obrigatorio(dto.nome(), "nome");
        String login = obrigatorio(dto.login(), "login");
        String senha = obrigatorio(dto.senha(), "senha");
        PerfilAcesso perfil = perfilPublico(dto.perfil());
        if (senha.length() < 8) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha deve conter pelo menos 8 caracteres");
        if (usuarios.findByLoginIgnoreCase(login).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login já cadastrado");
        }
        String curso = perfil == PerfilAcesso.ALUNO ? obrigatorio(dto.curso(), "curso") : "";
        if (perfil == PerfilAcesso.ALUNO && !cursos().contains(curso)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Curso não disponível");
        }
        UUID id = UUID.randomUUID();
        usuarios.save(new UsuarioJpaEntity(id, login, senhas.encode(senha), nome,
            "PEND-" + id.toString().replace("-", "").substring(0, 20), perfil, curso, "", false));
    }

    @Transactional(readOnly = true)
    public List<SolicitacaoPendenteDto> pendentes(PerfilAcesso perfil) {
        if (perfil == PerfilAcesso.ADMIN) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        return usuarios.findAll().stream().filter(u -> !u.isAtivo() && u.getPerfil() == perfil)
            .sorted(Comparator.comparing(UsuarioJpaEntity::getNome))
            .map(u -> new SolicitacaoPendenteDto(u.getId(), u.getNome(), u.getLogin(),
                u.getPerfil().name(), u.getCurso())).toList();
    }

    @Transactional
    public void rejeitar(UUID id) {
        UsuarioJpaEntity usuario = pendente(id);
        usuarios.delete(usuario);
    }

    private UsuarioJpaEntity pendente(UUID id) {
        UsuarioJpaEntity u = usuarios.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitação inexistente"));
        if (u.isAtivo()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Cadastro já aprovado");
        return u;
    }

    @Transactional
    public void aprovar(UUID id, AprovarCadastroDto dto) {
        UsuarioJpaEntity u = pendente(id);
        PerfilAcesso perfil = perfilPublico(dto.perfil());
        if (perfil == PerfilAcesso.ALUNO) {
            String curso = obrigatorio(dto.curso(), "curso");
            String codigo = obrigatorio(dto.turma(), "turma");
            String matricula = obrigatorio(dto.matricula(), "matricula");
            if (usuarios.findAll().stream().anyMatch(outro -> !outro.getId().equals(id) && matricula.equalsIgnoreCase(outro.getMatricula()))) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Matrícula já utilizada");
            }
            boolean valida = turmas.findAll().stream().anyMatch(t -> t.getCodigo().equals(codigo) && t.getCurso().equals(curso));
            if (!valida) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Turma não pertence ao curso");
            u.aprovar(perfil, curso, codigo, matricula);
        } else {
            List<AprovarCadastroDto.AtribuicaoDocenteDto> atribuicoes = dto.atribuicoes();
            if (atribuicoes == null || atribuicoes.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione pelo menos uma turma e UC");
            }
            Map<String, TurmaProfessorJpaEntity> modelos = new LinkedHashMap<>();
            Set<String> combinacoes = new HashSet<>();
            for (var a : atribuicoes) {
                String codigo = obrigatorio(a.turma(), "turma");
                String uc = obrigatorio(a.unidadeCurricular(), "unidadeCurricular");
                if (!combinacoes.add(codigo + "|" + uc)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Atribuição duplicada");
                TurmaProfessorJpaEntity modelo = turmas.findAll().stream()
                    .filter(t -> t.getCodigo().equals(codigo)).findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Turma inexistente: " + codigo));
                boolean ucValida = unidades.findAll().stream()
                    .anyMatch(x -> x.getTurma().equals(codigo) && x.getNome().equals(uc));
                if (!ucValida) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UC não pertence à turma: " + uc);
                modelos.put(codigo, modelo);
            }
            Map<String, UUID> novasTurmas = new HashMap<>();
            for (var entry : modelos.entrySet()) {
                TurmaProfessorJpaEntity t = entry.getValue();
                UUID turmaId = UUID.randomUUID();
                novasTurmas.put(entry.getKey(), turmaId);
                turmas.save(new TurmaProfessorJpaEntity(turmaId, id, t.getCodigo(), t.getCurso(),
                    t.getSemestre(), t.getQuantidadeAlunos(), t.getDiasSemana(), t.getHorarioInicio(), t.getHorarioFim()));
            }
            for (var a : atribuicoes) {
                UnidadeCurricularProfessorJpaEntity uc = unidades.findAll().stream()
                    .filter(x -> x.getTurma().equals(a.turma()) && x.getNome().equals(a.unidadeCurricular()))
                    .findFirst().orElseThrow();
                unidades.save(new UnidadeCurricularProfessorJpaEntity(UUID.randomUUID(), id, novasTurmas.get(a.turma()),
                    a.turma(), uc.getNome(), uc.getCargaHoraria(), uc.getQuantidadeAulas(), 0,
                    uc.getDiasSemana(), uc.getHorarioInicio(), uc.getHorarioFim()));
            }
            u.aprovar(perfil, "", "", "PROF-" + id.toString().replace("-", "").substring(0, 20));
        }
        usuarios.save(u);
    }
}
