package br.senai.carteirinha.modules.usuario.presentation;
import br.senai.carteirinha.modules.usuario.application.dto.SolicitacaoCadastroDto;
import br.senai.carteirinha.modules.usuario.application.service.GestaoSolicitacoesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/public")
public class CadastroController {
    private final GestaoSolicitacoesService service;
    public CadastroController(GestaoSolicitacoesService service) { this.service = service; }
    @GetMapping("/cursos")
    public List<String> cursos() { return service.cursos(); }
    @PostMapping("/cadastros")
    public ResponseEntity<Void> cadastrar(@RequestBody SolicitacaoCadastroDto dto) {
        service.cadastrar(dto);
        return ResponseEntity.accepted().build();
    }
}
