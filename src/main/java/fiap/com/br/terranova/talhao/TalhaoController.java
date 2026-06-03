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

@RestController
@RequestMapping("/api/talhoes")
@RequiredArgsConstructor
public class TalhaoController {

    private final TalhaoService service;

    @GetMapping
    public ResponseEntity<Page<EntityModel<TalhaoResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<TalhaoResponse>> page = service.findAll(pageable).map(TalhaoResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/produtor/{idProdutor}")
    public ResponseEntity<CollectionModel<EntityModel<TalhaoResponse>>> findByProdutor(@PathVariable Long idProdutor) {
        List<EntityModel<TalhaoResponse>> models = service.findByProdutor(idProdutor).stream()
                .map(TalhaoResponse::toEntityModel)
                .collect(Collectors.toList());
        var linkSelf = linkTo(methodOn(TalhaoController.class).findByProdutor(idProdutor)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(models, linkSelf));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TalhaoResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @PostMapping
    public ResponseEntity<EntityModel<TalhaoResponse>> create(@RequestBody @Valid TalhaoRequest request) {
        return new ResponseEntity<>(service.create(request).toEntityModel(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<TalhaoResponse>> update(@PathVariable Long id, @RequestBody @Valid TalhaoRequest request) {
        return ResponseEntity.ok(service.update(id, request).toEntityModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
