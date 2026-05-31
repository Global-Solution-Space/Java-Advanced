package fiap.com.br.terranova.alerta;

import fiap.com.br.terranova.alerta.dto.AlertaRequest;
import fiap.com.br.terranova.alerta.dto.AlertaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService service;

    @GetMapping
    public ResponseEntity<Page<EntityModel<AlertaResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<AlertaResponse>> page = service.findAll(pageable).map(AlertaResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<AlertaResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @PostMapping
    public ResponseEntity<EntityModel<AlertaResponse>> create(@RequestBody @Valid AlertaRequest request) {
        return new ResponseEntity<>(service.create(request).toEntityModel(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<AlertaResponse>> update(@PathVariable Long id, @RequestBody @Valid AlertaRequest request) {
        return ResponseEntity.ok(service.update(id, request).toEntityModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}