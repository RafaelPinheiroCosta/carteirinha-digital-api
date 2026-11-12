package br.senai.carteirinha.modules.usuario.presentation;
import br.senai.carteirinha.modules.usuario.application.dto.*;
import br.senai.carteirinha.modules.usuario.application.service.GestaoSolicitacoesService;
import br.senai.carteirinha.modules.usuario.domain.PerfilAcesso;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/admin")
public class GestaoSolicitacoesController {
    private final GestaoSolicitacoesService service;
    public GestaoSolicitacoesController(GestaoSolicitacoesService service) { this.service = service; }
    @GetMapping("/solicitacoes")
    public List<SolicitacaoPendenteDto> pendentes(@RequestParam PerfilAcesso perfil) { return service.pendentes(perfil); }
    @GetMapping("/catalogo/turmas")
    public List<TurmaCatalogoDto> turmas() { return service.catalogo(); }
    @PutMapping("/solicitacoes/{id}/aprovacao")
    public ResponseEntity<Void> aprovar(@PathVariable UUID id, @RequestBody AprovarCadastroDto dto) {
        service.aprovar(id, dto); return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/solicitacoes/{id}")
    public ResponseEntity<Void> rejeitar(@PathVariable UUID id) {
        service.rejeitar(id); return ResponseEntity.noContent().build();
    }
}
