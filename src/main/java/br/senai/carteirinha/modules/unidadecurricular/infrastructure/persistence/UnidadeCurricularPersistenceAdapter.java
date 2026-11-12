package br.senai.carteirinha.modules.unidadecurricular.infrastructure.persistence;

import br.senai.carteirinha.modules.curso.infrastructure.persistence.*;
import br.senai.carteirinha.modules.professor.infrastructure.persistence.*;
import br.senai.carteirinha.modules.usuario.domain.PerfilAcesso;
import br.senai.carteirinha.modules.usuario.infrastructure.persistence.*;
import br.senai.carteirinha.modules.unidadecurricular.application.port.out.UnidadeCurricularRepository;
import br.senai.carteirinha.modules.unidadecurricular.domain.UnidadeCurricular;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class UnidadeCurricularPersistenceAdapter implements UnidadeCurricularRepository {
    private final SpringDataUsuarioRepository usuarios;
    private final SpringDataCursoRepository cursos;
    private final SpringDataUnidadeCurricularCursoRepository unidadesCurso;
    private final SpringDataUnidadeCurricularProfessorRepository atribuicoes;
    private final SpringDataUnidadeCurricularRepository notasLegadas;

    public UnidadeCurricularPersistenceAdapter(
        SpringDataUsuarioRepository usuarios,
        SpringDataCursoRepository cursos,
        SpringDataUnidadeCurricularCursoRepository unidadesCurso,
        SpringDataUnidadeCurricularProfessorRepository atribuicoes,
        SpringDataUnidadeCurricularRepository notasLegadas
    ) {
        this.usuarios = usuarios;
        this.cursos = cursos;
        this.unidadesCurso = unidadesCurso;
        this.atribuicoes = atribuicoes;
        this.notasLegadas = notasLegadas;
    }

    @Override
    public List<UnidadeCurricular> listarPorUsuario(UUID usuarioId) {
        UsuarioJpaEntity aluno = usuarios.findById(usuarioId)
            .filter(u -> u.isAtivo() && u.getPerfil() == PerfilAcesso.ALUNO)
            .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado ou inativo"));
        CursoJpaEntity curso = cursos.findAll().stream()
            .filter(c -> c.getNome().equals(aluno.getCurso()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Curso da matrícula não encontrado"));

        Map<String, UnidadeCurricularJpaEntity> historico = new HashMap<>();
        for (var nota : notasLegadas.findByUsuarioIdOrderByNomeAsc(usuarioId)) {
            historico.putIfAbsent(nota.getNome(), nota);
        }

        // A grade vem do curso. Dados individuais de nota/faltas são históricos opcionais.
        // Docente é resolvido por turma e UC; não é copiado para o aluno.
        List<UnidadeCurricularProfessorJpaEntity> vinculos = atribuicoes.findAll().stream()
            .filter(v -> v.getTurma().equals(aluno.getTurma()))
            .sorted(Comparator.comparing(v -> v.getId().toString()))
            .toList();

        return unidadesCurso.findByCursoCodigoOrderByNomeAsc(curso.getCodigo()).stream()
            .map(uc -> {
                UnidadeCurricularJpaEntity nota = historico.get(uc.getNome());
                String professor = vinculos.stream()
                    .filter(v -> v.getNome().equals(uc.getNome()))
                    .map(v -> usuarios.findById(v.getProfessorId()).orElse(null))
                    .filter(Objects::nonNull)
                    .filter(u -> u.isAtivo() && u.getPerfil() == PerfilAcesso.PROFESSOR)
                    .map(UsuarioJpaEntity::getNome)
                    .distinct()
                    .sorted()
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("Professor a definir");
                return new UnidadeCurricular(
                    uc.getId(),
                    usuarioId,
                    uc.getNome(),
                    professor,
                    nota == null ? 0 : nota.getNota1(),
                    nota == null ? 0 : nota.getNota2(),
                    nota == null ? 0 : nota.getFaltas()
                );
            })
            .toList();
    }
}
