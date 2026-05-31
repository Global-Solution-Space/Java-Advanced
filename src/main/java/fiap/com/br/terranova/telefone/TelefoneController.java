package fiap.com.br.terranova.telefone;

import fiap.com.br.terranova.telefone.dto.TelefoneRequest;
import fiap.com.br.terranova.telefone.dto.TelefoneResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;

@RestController
@RequestMapping("/api/telefones")
@RequiredArgsConstructor
public class TelefoneController {

    private final TelefoneService service;

    @GetMapping
    public ResponseEntity<Page<EntityModel<TelefoneResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<TelefoneResponse>> page = service.findAll(pageable).map(TelefoneResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TelefoneResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @PostMapping
    public ResponseEntity<EntityModel<TelefoneResponse>> create(@RequestBody @Valid TelefoneRequest request) {
        return new ResponseEntity<>(service.create(request).toEntityModel(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<TelefoneResponse>> update(@PathVariable Long id, @RequestBody @Valid TelefoneRequest request) {
        return ResponseEntity.ok(service.update(id, request).toEntityModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
