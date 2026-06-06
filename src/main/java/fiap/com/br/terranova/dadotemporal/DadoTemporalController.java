package fiap.com.br.terranova.dadotemporal;

import fiap.com.br.terranova.dadotemporal.dto.DadoTemporalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/dados-temporais")
@RequiredArgsConstructor
@Tag(name = "Dado Temporal")
public class DadoTemporalController {

    private final DadoTemporalService service;

    @Operation(summary = "Buscar todos os registros", description = "Retorna uma lista com todos os registros de dados temporais.")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<DadoTemporalResponse>>> findAll() {
        List<EntityModel<DadoTemporalResponse>> models = service.findAll().stream()
                .map(DadoTemporalResponse::toEntityModel)
                .collect(Collectors.toList());

        var linkSelf = linkTo(methodOn(DadoTemporalController.class).findAll()).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(models, linkSelf));
    }

    @Operation(summary = "Buscar por ID", description = "Retorna o registro específico baseado no ID informado.")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<DadoTemporalResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @Operation(summary = "Buscar por Talhão", description = "Retorna os registros associados ao ID do talhão.")
    @GetMapping("/talhao/{idTalhao}")
    public ResponseEntity<Page<EntityModel<DadoTemporalResponse>>> findByTalhao(
            @PathVariable Long idTalhao,
            @PageableDefault(size = 100, sort = "idDado", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<EntityModel<DadoTemporalResponse>> page = service.findByTalhao(idTalhao, pageable)
                .map(DadoTemporalResponse::toEntityModel);
        
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Buscar por Integração", description = "Retorna os registros de dados temporais associados a uma requisição de API externa.")
    @GetMapping("/req-api/{idReqApi}")
    public ResponseEntity<Page<EntityModel<DadoTemporalResponse>>> findByReqApi(
            @PathVariable Long idReqApi,
            @PageableDefault(size = 100, sort = "idDado", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<EntityModel<DadoTemporalResponse>> page = service.findByReqApi(idReqApi, pageable)
                .map(DadoTemporalResponse::toEntityModel);
                
        return ResponseEntity.ok(page);
    }
}
