package fiap.com.br.terranova.localizacao;

import fiap.com.br.terranova.localizacao.dto.LocalizacaoRequest;
import fiap.com.br.terranova.localizacao.dto.LocalizacaoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;

@RestController
@RequestMapping("/api/localizacoes")
@RequiredArgsConstructor
public class LocalizacaoController {

    private final LocalizacaoService service;

    @GetMapping
    public ResponseEntity<Page<EntityModel<LocalizacaoResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<LocalizacaoResponse>> page = service.findAll(pageable).map(LocalizacaoResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<LocalizacaoResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @PostMapping
    public ResponseEntity<EntityModel<LocalizacaoResponse>> create(@RequestBody @Valid LocalizacaoRequest request) {
        return new ResponseEntity<>(service.create(request).toEntityModel(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<LocalizacaoResponse>> update(@PathVariable Long id, @RequestBody @Valid LocalizacaoRequest request) {
        return ResponseEntity.ok(service.update(id, request).toEntityModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}