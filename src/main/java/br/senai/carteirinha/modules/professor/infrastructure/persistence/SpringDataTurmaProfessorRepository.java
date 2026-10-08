package br.senai.carteirinha.modules.professor.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataTurmaProfessorRepository
    extends JpaRepository<
        TurmaProfessorJpaEntity,
        UUID
    > {

    List<TurmaProfessorJpaEntity>
    findByProfessorIdOrderByCodigoAsc(
        UUID professorId
    );
}