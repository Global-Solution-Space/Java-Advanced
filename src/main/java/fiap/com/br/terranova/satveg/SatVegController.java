package fiap.com.br.terranova.satveg;

import fiap.com.br.terranova.satveg.dto.SatVegRequest;
import fiap.com.br.terranova.satveg.dto.SatVegResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/satveg")
public class SatVegController {

    private final SatVegService service;

    public SatVegController(SatVegService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SatVegResponse> criar(@RequestBody SatVegRequest request) {
        return new ResponseEntity<>(service.criar(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SatVegResponse>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SatVegResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SatVegResponse> atualizar(@PathVariable Long id, @RequestBody SatVegRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}