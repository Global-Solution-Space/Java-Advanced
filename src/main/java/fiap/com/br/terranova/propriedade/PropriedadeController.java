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

@RestController
@RequestMapping("/api/propriedades")
@RequiredArgsConstructor
public class PropriedadeController {

    private final PropriedadeService service;

    @GetMapping
    public ResponseEntity<Page<EntityModel<PropriedadeResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<PropriedadeResponse>> page = service.findAll(pageable).map(PropriedadeResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/produtor/{idProdutor}")
    public ResponseEntity<CollectionModel<EntityModel<PropriedadeResponse>>> findByProdutor(@PathVariable Long idProdutor) {
        List<EntityModel<PropriedadeResponse>> models = service.findByProdutor(idProdutor).stream()
                .map(PropriedadeResponse::toEntityModel)
                .collect(Collectors.toList());
        var linkSelf = linkTo(methodOn(PropriedadeController.class).findByProdutor(idProdutor)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(models, linkSelf));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PropriedadeResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @PostMapping
    public ResponseEntity<EntityModel<PropriedadeResponse>> create(@RequestBody @Valid PropriedadeRequest request) {
        return new ResponseEntity<>(service.create(request).toEntityModel(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<PropriedadeResponse>> update(@PathVariable Long id, @RequestBody @Valid PropriedadeRequest request) {
        return ResponseEntity.ok(service.update(id, request).toEntityModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
