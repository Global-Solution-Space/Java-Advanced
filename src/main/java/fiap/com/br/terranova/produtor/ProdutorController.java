package fiap.com.br.terranova.produtor;

import fiap.com.br.terranova.produtor.dto.ProdutorRequest;
import fiap.com.br.terranova.produtor.dto.ProdutorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;

@RestController
@RequestMapping("/api/produtores")
@RequiredArgsConstructor
public class ProdutorController {

    private final ProdutorService service;

    @GetMapping
    public ResponseEntity<Page<EntityModel<ProdutorResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<ProdutorResponse>> page = service.findAll(pageable).map(ProdutorResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProdutorResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @PostMapping
    public ResponseEntity<EntityModel<ProdutorResponse>> create(@RequestBody @Valid ProdutorRequest request) {
        return new ResponseEntity<>(service.create(request).toEntityModel(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ProdutorResponse>> update(@PathVariable Long id, @RequestBody @Valid ProdutorRequest request) {
        return ResponseEntity.ok(service.update(id, request).toEntityModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
