package br.senai.carteirinha.modules.professor.infrastructure.persistence;

import br.senai.carteirinha.modules.professor.application.port.out.TurmaProfessorRepository;
import br.senai.carteirinha.modules.professor.application.port.out.UnidadeCurricularProfessorRepository;
import br.senai.carteirinha.modules.professor.domain.TurmaProfessor;
import br.senai.carteirinha.modules.professor.domain.UnidadeCurricularProfessor;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Repository
public class ProfessorPersistenceAdapter
    implements
        TurmaProfessorRepository,
        UnidadeCurricularProfessorRepository {

    private final SpringDataTurmaProfessorRepository
        turmaRepository;

    private final SpringDataUnidadeCurricularProfessorRepository
        unidadeRepository;

    public ProfessorPersistenceAdapter(
        SpringDataTurmaProfessorRepository turmaRepository,
        SpringDataUnidadeCurricularProfessorRepository unidadeRepository
    ) {
        this.turmaRepository =
            turmaRepository;

        this.unidadeRepository =
            unidadeRepository;
    }

    @Override
    public List<TurmaProfessor>
        listarTurmasPorProfessor(
            UUID professorId
        ) {

        return turmaRepository
            .findByProfessorIdOrderByCodigoAsc(
                professorId
            )
            .stream()
            .map(
                this::toTurmaDomain
            )
            .toList();
    }

    @Override
    public List<UnidadeCurricularProfessor>
        listarUnidadesCurricularesPorProfessor(
            UUID professorId
        ) {

        return unidadeRepository
            .findByProfessorIdOrderByNomeAscTurmaAsc(
                professorId
            )
            .stream()
            .map(
                this::toUnidadeDomain
            )
            .toList();
    }

    private TurmaProfessor toTurmaDomain(
        TurmaProfessorJpaEntity entity
    ) {

        return new TurmaProfessor(
            entity.getId(),
            entity.getProfessorId(),
            entity.getCodigo(),
            entity.getCurso(),
            entity.getSemestre(),
            entity.getQuantidadeAlunos(),
            converterDias(
                entity.getDiasSemana()
            ),
            entity.getHorarioInicio(),
            entity.getHorarioFim()
        );
    }

    private UnidadeCurricularProfessor toUnidadeDomain(
        UnidadeCurricularProfessorJpaEntity entity
    ) {

        return new UnidadeCurricularProfessor(
            entity.getId(),
            entity.getProfessorId(),
            entity.getTurmaId(),
            entity.getTurma(),
            entity.getNome(),
            entity.getCargaHoraria(),
            entity.getQuantidadeAulas(),
            entity.getAulasRealizadas(),
            converterDias(
                entity.getDiasSemana()
            ),
            entity.getHorarioInicio(),
            entity.getHorarioFim()
        );
    }

    private List<String> converterDias(
        String dias
    ) {

        return Arrays.stream(
                dias.split("\\|")
            )
            .map(
                String::trim
            )
            .filter(
                valor ->
                    !valor.isBlank()
            )
            .toList();
    }
}