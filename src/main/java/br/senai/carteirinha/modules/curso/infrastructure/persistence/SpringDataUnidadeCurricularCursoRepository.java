package br.senai.carteirinha.modules.curso.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SpringDataUnidadeCurricularCursoRepository
    extends JpaRepository<UnidadeCurricularCursoJpaEntity, UUID> {
    List<UnidadeCurricularCursoJpaEntity> findByCursoCodigoOrderByNomeAsc(String cursoCodigo);
}
