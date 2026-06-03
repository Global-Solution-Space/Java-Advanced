package fiap.com.br.terranova.reqapi;

import fiap.com.br.terranova.reqapi.dto.ReqApiRequest;
import fiap.com.br.terranova.reqapi.dto.ReqApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;

@RestController
@RequestMapping("/api/req-api")
@RequiredArgsConstructor
public class ReqApiController {

    private final ReqApiService service;

    @GetMapping
    public ResponseEntity<Page<EntityModel<ReqApiResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<ReqApiResponse>> page = service.findAll(pageable).map(ReqApiResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ReqApiResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @PostMapping
    public ResponseEntity<EntityModel<ReqApiResponse>> create(@RequestBody @Valid ReqApiRequest request) {
        return new ResponseEntity<>(service.create(request).toEntityModel(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ReqApiResponse>> update(@PathVariable Long id, @RequestBody @Valid ReqApiRequest request) {
        return ResponseEntity.ok(service.update(id, request).toEntityModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
