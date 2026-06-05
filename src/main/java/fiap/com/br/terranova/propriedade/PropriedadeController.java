package fiap.com.br.terranova.propriedade;

import fiap.com.br.terranova.propriedade.dto.PropriedadeRequest;
import fiap.com.br.terranova.propriedade.dto.PropriedadeResponse;
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
@RequestMapping("/api/propriedades")
@RequiredArgsConstructor
@Tag(name = "Propriedade")
public class PropriedadeController {

    private final PropriedadeService service;

    @Operation(summary = "Listar todos", description = "Retorna uma página com todos os registros.")
    @GetMapping
    public ResponseEntity<Page<EntityModel<PropriedadeResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<PropriedadeResponse>> page = service.findAll(pageable).map(PropriedadeResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Buscar por ID", description = "Retorna o registro específico baseado no ID informado.")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PropriedadeResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @Operation(summary = "Buscar por Produtor", description = "Retorna os registros associados ao ID do produtor.")
    @GetMapping("/produtor/{idProdutor}")
    public ResponseEntity<CollectionModel<EntityModel<PropriedadeResponse>>> findByProdutor(@PathVariable Long idProdutor) {
        List<EntityModel<PropriedadeResponse>> models = service.findByProdutor(idProdutor).stream()
                .map(PropriedadeResponse::toEntityModel)
                .collect(Collectors.toList());
        var linkSelf = linkTo(methodOn(PropriedadeController.class).findByProdutor(idProdutor)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(models, linkSelf));
    }

    @Operation(summary = "Cadastrar", description = "Cadastra um novo registro no sistema.")
    @PostMapping
    public ResponseEntity<EntityModel<PropriedadeResponse>> create(@RequestBody @Valid PropriedadeRequest request) {
        return new ResponseEntity<>(service.create(request).toEntityModel(), HttpStatus.CREATED);
    }

    @Operation(summary = "Atualizar", description = "Atualiza os dados de um registro existente.")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<PropriedadeResponse>> update(@PathVariable Long id, @RequestBody @Valid PropriedadeRequest request) {
        return ResponseEntity.ok(service.update(id, request).toEntityModel());
    }

    @Operation(summary = "Deletar", description = "Remove um registro do sistema pelo ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
