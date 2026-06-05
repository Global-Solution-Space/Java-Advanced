package fiap.com.br.terranova.reqapi;

import fiap.com.br.terranova.reqapi.dto.ReqApiRequest;
import fiap.com.br.terranova.reqapi.dto.ReqApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/req-api")
@RequiredArgsConstructor
@Tag(name = "Integração (Req API)")
public class ReqApiController {

    private final ReqApiService service;

    @Operation(summary = "Listar todos", description = "Retorna uma página com todos os registros.")
    @GetMapping
    public ResponseEntity<Page<EntityModel<ReqApiResponse>>> findAll(Pageable pageable) {
        Page<EntityModel<ReqApiResponse>> page = service.findAll(pageable).map(ReqApiResponse::toEntityModel);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Buscar por ID", description = "Retorna o registro específico baseado no ID informado.")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ReqApiResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @Operation(summary = "Buscar por Talhão", description = "Retorna o histórico de requisições de um Talhão específico.")
    @GetMapping("/talhao/{idTalhao}")
    public ResponseEntity<List<EntityModel<ReqApiResponse>>> findByTalhao(@PathVariable Long idTalhao) {
        List<EntityModel<ReqApiResponse>> list = service.findByTalhaoId(idTalhao).stream()
                .map(ReqApiResponse::toEntityModel)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Cadastrar", description = "Cadastra um novo registro no sistema.")
    @PostMapping
    public ResponseEntity<EntityModel<ReqApiResponse>> create(@RequestBody @Valid ReqApiRequest request) {
        return new ResponseEntity<>(service.create(request).toEntityModel(), HttpStatus.CREATED);
    }


    @Operation(summary = "Deletar", description = "Remove um registro do sistema pelo ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
