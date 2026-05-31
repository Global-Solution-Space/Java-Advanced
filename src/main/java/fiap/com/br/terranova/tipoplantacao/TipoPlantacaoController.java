package fiap.com.br.terranova.tipoplantacao;

import fiap.com.br.terranova.tipoplantacao.dto.TipoPlantacaoRequest;
import fiap.com.br.terranova.tipoplantacao.dto.TipoPlantacaoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-plantacao")
public class TipoPlantacaoController {

    private final TipoPlantacaoService service;

    public TipoPlantacaoController(TipoPlantacaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TipoPlantacaoResponse> criar(@RequestBody TipoPlantacaoRequest request) {
        return new ResponseEntity<>(service.criar(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TipoPlantacaoResponse>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoPlantacaoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoPlantacaoResponse> atualizar(@PathVariable Long id, @RequestBody TipoPlantacaoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}