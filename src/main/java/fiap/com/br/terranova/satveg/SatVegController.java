package fiap.com.br.terranova.satveg;

import fiap.com.br.terranova.satveg.dto.SatVegRequest;
import fiap.com.br.terranova.satveg.dto.SatVegResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;

@RestController
@RequestMapping("/api/satveg")
@RequiredArgsConstructor
public class SatVegController {

    private final SatVegService service;

    @GetMapping
    public ResponseEntity<Page<EntityModel<SatVegResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<SatVegResponse>> page = service.findAll(pageable).map(SatVegResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<SatVegResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @PostMapping
    public ResponseEntity<EntityModel<SatVegResponse>> create(@RequestBody @Valid SatVegRequest request) {
        return new ResponseEntity<>(service.create(request).toEntityModel(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<SatVegResponse>> update(@PathVariable Long id, @RequestBody @Valid SatVegRequest request) {
        return ResponseEntity.ok(service.update(id, request).toEntityModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}