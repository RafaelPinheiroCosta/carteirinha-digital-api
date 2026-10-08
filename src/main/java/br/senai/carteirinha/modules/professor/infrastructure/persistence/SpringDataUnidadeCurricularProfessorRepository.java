package br.senai.carteirinha.modules.professor.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataUnidadeCurricularProfessorRepository
    extends JpaRepository<
        UnidadeCurricularProfessorJpaEntity,
        UUID
    > {

    List<UnidadeCurricularProfessorJpaEntity>
    findByProfessorIdOrderByNomeAscTurmaAsc(
        UUID professorId
    );
}