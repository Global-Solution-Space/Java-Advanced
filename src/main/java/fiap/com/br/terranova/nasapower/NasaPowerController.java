package fiap.com.br.terranova.nasapower;

import fiap.com.br.terranova.nasapower.dto.NasaPowerRequest;
import fiap.com.br.terranova.nasapower.dto.NasaPowerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;

@RestController
@RequestMapping("/api/nasapower")
@RequiredArgsConstructor
public class NasaPowerController {

    private final NasaPowerService service;

    @GetMapping
    public ResponseEntity<Page<EntityModel<NasaPowerResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<NasaPowerResponse>> page = service.findAll(pageable).map(NasaPowerResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<NasaPowerResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @PostMapping
    public ResponseEntity<EntityModel<NasaPowerResponse>> create(@RequestBody @Valid NasaPowerRequest request) {
        return new ResponseEntity<>(service.create(request).toEntityModel(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<NasaPowerResponse>> update(@PathVariable Long id, @RequestBody @Valid NasaPowerRequest request) {
        return ResponseEntity.ok(service.update(id, request).toEntityModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}