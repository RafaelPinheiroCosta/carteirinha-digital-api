package br.senai.carteirinha.modules.professor.presentation;

import br.senai.carteirinha.modules.professor.application.dto.TurmaProfessorResponseDto;
import br.senai.carteirinha.modules.professor.application.dto.UnidadeCurricularProfessorResponseDto;
import br.senai.carteirinha.modules.professor.application.port.in.ListarTurmasProfessorUseCase;
import br.senai.carteirinha.modules.professor.application.port.in.ListarUnidadesCurricularesProfessorUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/professores/me")
@Tag(
    name = "Professor"
)
public class ProfessorController {

    private final ListarTurmasProfessorUseCase
        listarTurmas;

    private final ListarUnidadesCurricularesProfessorUseCase
        listarUnidadesCurriculares;

    public ProfessorController(
        ListarTurmasProfessorUseCase listarTurmas,
        ListarUnidadesCurricularesProfessorUseCase
            listarUnidadesCurriculares
    ) {
        this.listarTurmas =
            listarTurmas;

        this.listarUnidadesCurriculares =
            listarUnidadesCurriculares;
    }

    @GetMapping("/turmas")
    @Operation(
        summary =
            "Lista as turmas do professor autenticado"
    )
    public List<TurmaProfessorResponseDto> listarTurmas(
        @AuthenticationPrincipal
        Jwt jwt
    ) {

        UUID professorId =
            extrairUsuarioId(
                jwt
            );

        return listarTurmas
            .listar(
                professorId
            )
            .stream()
            .map(
                TurmaProfessorResponseDto::from
            )
            .toList();
    }

    @GetMapping("/unidades-curriculares")
    @Operation(
        summary =
            "Lista as unidades curriculares ministradas pelo professor autenticado"
    )
    public List<UnidadeCurricularProfessorResponseDto>
        listarUnidadesCurriculares(
            @AuthenticationPrincipal
            Jwt jwt
        ) {

        UUID professorId =
            extrairUsuarioId(
                jwt
            );

        return listarUnidadesCurriculares
            .listar(
                professorId
            )
            .stream()
            .map(
                UnidadeCurricularProfessorResponseDto::from
            )
            .toList();
    }

    private UUID extrairUsuarioId(
        Jwt jwt
    ) {

        return UUID.fromString(
            jwt.getClaimAsString(
                "usuarioId"
            )
        );
    }
}