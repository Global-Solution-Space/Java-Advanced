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

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/tipos-plantacao")
@RequiredArgsConstructor
@Tag(name = "Tipo de Plantação")
public class TipoPlantacaoController {

    private final TipoPlantacaoService service;

    @Operation(summary = "Listar todos", description = "Retorna uma página com todos os registros.")
    @GetMapping
    public ResponseEntity<Page<EntityModel<TipoPlantacaoResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<TipoPlantacaoResponse>> page = service.findAll(pageable)
                .map(TipoPlantacaoResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Buscar por ID", description = "Retorna o registro específico baseado no ID informado.")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TipoPlantacaoResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @Operation(summary = "Cadastrar", description = "Cadastra um novo registro no sistema.")
    @PostMapping
    public ResponseEntity<EntityModel<TipoPlantacaoResponse>> create(@RequestBody @Valid TipoPlantacaoRequest request) {
        return new ResponseEntity<>(service.create(request).toEntityModel(), HttpStatus.CREATED);
    }

    @Operation(summary = "Atualizar", description = "Atualiza os dados de um registro existente.")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<TipoPlantacaoResponse>> update(@PathVariable Long id,
            @RequestBody @Valid TipoPlantacaoRequest request) {
        return ResponseEntity.ok(service.update(id, request).toEntityModel());
    }

    @Operation(summary = "Deletar", description = "Remove um registro do sistema pelo ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}