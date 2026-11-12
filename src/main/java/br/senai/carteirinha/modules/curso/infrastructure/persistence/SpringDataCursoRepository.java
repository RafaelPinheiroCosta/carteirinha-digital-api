package br.senai.carteirinha.modules.curso.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataCursoRepository extends JpaRepository<CursoJpaEntity, String> {}
