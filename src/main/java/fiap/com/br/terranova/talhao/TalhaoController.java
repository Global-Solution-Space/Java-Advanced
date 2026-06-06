package fiap.com.br.terranova.talhao;

import fiap.com.br.terranova.talhao.dto.TalhaoRequest;
import fiap.com.br.terranova.talhao.dto.TalhaoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/talhoes")
@RequiredArgsConstructor
@Tag(name = "Talhão")
public class TalhaoController {

    private final TalhaoService service;

    @Operation(summary = "Listar todos", description = "Retorna uma página com todos os registros.")
    @GetMapping
    public ResponseEntity<Page<EntityModel<TalhaoResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<TalhaoResponse>> page = service.findAll(pageable).map(TalhaoResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Buscar por ID", description = "Retorna o registro específico baseado no ID informado.")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TalhaoResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @Operation(summary = "Buscar por Produtor", description = "Retorna os registros associados ao ID do produtor.")
    @GetMapping("/produtor/{idProdutor}")
    public ResponseEntity<CollectionModel<EntityModel<TalhaoResponse>>> findByProdutor(@PathVariable Long idProdutor) {
        List<EntityModel<TalhaoResponse>> models = service.findByProdutor(idProdutor).stream()
                .map(TalhaoResponse::toEntityModel)
                .collect(Collectors.toList());
        var linkSelf = linkTo(methodOn(TalhaoController.class).findByProdutor(idProdutor)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(models, linkSelf));
    }

    @Operation(summary = "Cadastrar", description = "Cadastra um novo registro no sistema.")
    @PostMapping
    public ResponseEntity<EntityModel<TalhaoResponse>> create(@RequestBody @Valid TalhaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request).toEntityModel());
    }

    @Operation(summary = "Atualizar", description = "Atualiza os dados de um registro existente.")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<TalhaoResponse>> update(@PathVariable Long id, @RequestBody @Valid TalhaoRequest request) {
        return ResponseEntity.ok(service.update(id, request).toEntityModel());
    }

    @Operation(summary = "Deletar", description = "Remove um registro do sistema pelo ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
