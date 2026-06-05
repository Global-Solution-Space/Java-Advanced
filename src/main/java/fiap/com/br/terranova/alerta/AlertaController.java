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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
@Tag(name = "Alerta Agrícola")
public class AlertaController {

    private final AlertaService service;

    @Operation(summary = "Listar todos", description = "Retorna uma página com todos os registros.")
    @GetMapping
    public ResponseEntity<Page<EntityModel<AlertaResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<AlertaResponse>> page = service.findAll(pageable).map(AlertaResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Buscar por ID", description = "Retorna o registro específico baseado no ID informado.")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<AlertaResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @Operation(summary = "Buscar por Produtor", description = "Retorna os registros associados ao ID do produtor.")
    @GetMapping("/produtor/{idProdutor}")
    public ResponseEntity<CollectionModel<EntityModel<AlertaResponse>>> findByProdutor(@PathVariable Long idProdutor) {
        List<EntityModel<AlertaResponse>> models = service.findByProdutor(idProdutor).stream()
                .map(AlertaResponse::toEntityModel)
                .collect(Collectors.toList());
        var linkSelf = linkTo(methodOn(AlertaController.class).findByProdutor(idProdutor)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(models, linkSelf));
    }

    @Operation(summary = "Cadastrar", description = "Cadastra um novo registro no sistema.")
    @PostMapping
    public ResponseEntity<EntityModel<AlertaResponse>> create(@RequestBody @Valid AlertaRequest request) {
        return new ResponseEntity<>(service.create(request).toEntityModel(), HttpStatus.CREATED);
    }

    @Operation(summary = "Atualizar", description = "Atualiza os dados de um registro existente.")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<AlertaResponse>> update(@PathVariable Long id, @RequestBody @Valid AlertaRequest request) {
        return ResponseEntity.ok(service.update(id, request).toEntityModel());
    }

    @Operation(summary = "Deletar", description = "Remove um registro do sistema pelo ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Resolver Alerta", description = "Marca o alerta como resolvido sem necessitar do payload completo.")
    @PatchMapping("/{id}/resolver")
    public ResponseEntity<EntityModel<AlertaResponse>> resolver(@PathVariable Long id) {
        return ResponseEntity.ok(service.resolver(id).toEntityModel());
    }

    @Operation(summary = "Reabrir Alerta", description = "Marca o alerta como não resolvido sem necessitar do payload completo.")
    @PatchMapping("/{id}/reabrir")
    public ResponseEntity<EntityModel<AlertaResponse>> reabrir(@PathVariable Long id) {
        return ResponseEntity.ok(service.reabrir(id).toEntityModel());
    }
}