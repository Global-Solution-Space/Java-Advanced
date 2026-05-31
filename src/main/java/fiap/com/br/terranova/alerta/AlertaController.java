package fiap.com.br.terranova.alerta;

import fiap.com.br.terranova.alerta.dto.AlertaRequest;
import fiap.com.br.terranova.alerta.dto.AlertaResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @PostMapping
    public ResponseEntity<AlertaResponse> criar(@RequestBody AlertaRequest request) {
        AlertaResponse novoAlerta = alertaService.criar(request);
        return new ResponseEntity<>(novoAlerta, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AlertaResponse>> listarTodos() {
        List<AlertaResponse> alertas = alertaService.listarTodos();
        return ResponseEntity.ok(alertas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertaResponse> buscarPorId(@PathVariable Long id) {
        AlertaResponse alerta = alertaService.buscarPorId(id);
        return ResponseEntity.ok(alerta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertaResponse> atualizar(@PathVariable Long id, @RequestBody AlertaRequest request) {
        AlertaResponse atualizado = alertaService.atualizar(id, request);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        alertaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}