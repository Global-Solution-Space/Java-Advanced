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

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService service;

    @GetMapping
    public ResponseEntity<Page<EntityModel<AlertaResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<AlertaResponse>> page = service.findAll(pageable).map(AlertaResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/produtor/{idProdutor}")
    public ResponseEntity<CollectionModel<EntityModel<AlertaResponse>>> findByProdutor(@PathVariable Long idProdutor) {
        List<EntityModel<AlertaResponse>> models = service.findByProdutor(idProdutor).stream()
                .map(AlertaResponse::toEntityModel)
                .collect(Collectors.toList());
        var linkSelf = linkTo(methodOn(AlertaController.class).findByProdutor(idProdutor)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(models, linkSelf));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<AlertaResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @PostMapping
    public ResponseEntity<EntityModel<AlertaResponse>> create(@RequestBody @Valid AlertaRequest request) {
        return new ResponseEntity<>(service.create(request).toEntityModel(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<AlertaResponse>> update(@PathVariable Long id, @RequestBody @Valid AlertaRequest request) {
        return ResponseEntity.ok(service.update(id, request).toEntityModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}