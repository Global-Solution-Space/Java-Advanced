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

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/produtores")
@RequiredArgsConstructor
@Tag(name = "Produtor")
public class ProdutorController {

    private final ProdutorService service;

    @Operation(summary = "Listar todos", description = "Retorna uma página com todos os registros.")
    @GetMapping
    public ResponseEntity<Page<EntityModel<ProdutorResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<ProdutorResponse>> page = service.findAll(pageable).map(ProdutorResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Buscar por ID", description = "Retorna o registro específico baseado no ID informado.")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProdutorResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @Operation(summary = "Cadastrar", description = "Cadastra um novo registro no sistema.")
    @PostMapping
    public ResponseEntity<EntityModel<ProdutorResponse>> create(@RequestBody @Valid ProdutorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request).toEntityModel());
    }

    @Operation(summary = "Atualizar", description = "Atualiza os dados de um registro existente.")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ProdutorResponse>> update(@PathVariable Long id, @RequestBody @Valid ProdutorRequest request) {
        return ResponseEntity.ok(service.update(id, request).toEntityModel());
    }

    @Operation(summary = "Deletar", description = "Remove um registro do sistema pelo ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
