package fiap.com.br.terranova.nasapower;

import fiap.com.br.terranova.nasapower.dto.NasaPowerRequest;
import fiap.com.br.terranova.nasapower.dto.NasaPowerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/nasapower")
public class NasaPowerController {

    private final NasaPowerService service;

    public NasaPowerController(NasaPowerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NasaPowerResponse> criar(@RequestBody NasaPowerRequest request) {
        return new ResponseEntity<>(service.criar(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<NasaPowerResponse>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NasaPowerResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NasaPowerResponse> atualizar(@PathVariable Long id, @RequestBody NasaPowerRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}