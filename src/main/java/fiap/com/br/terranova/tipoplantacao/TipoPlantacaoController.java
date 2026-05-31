package fiap.com.br.terranova.tipoplantacao;

import fiap.com.br.terranova.tipoplantacao.dto.TipoPlantacaoRequest;
import fiap.com.br.terranova.tipoplantacao.dto.TipoPlantacaoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;

@RestController
@RequestMapping("/api/tipos-plantacao")
@RequiredArgsConstructor
public class TipoPlantacaoController {

    private final TipoPlantacaoService service;

    @GetMapping
    public ResponseEntity<Page<EntityModel<TipoPlantacaoResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<TipoPlantacaoResponse>> page = service.findAll(pageable).map(TipoPlantacaoResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TipoPlantacaoResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @PostMapping
    public ResponseEntity<EntityModel<TipoPlantacaoResponse>> create(@RequestBody @Valid TipoPlantacaoRequest request) {
        return new ResponseEntity<>(service.create(request).toEntityModel(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<TipoPlantacaoResponse>> update(@PathVariable Long id, @RequestBody @Valid TipoPlantacaoRequest request) {
        return ResponseEntity.ok(service.update(id, request).toEntityModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}