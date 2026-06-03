package fiap.com.br.terranova.dadotemporal;

import fiap.com.br.terranova.dadotemporal.dto.DadoTemporalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/dados-temporais")
@RequiredArgsConstructor
public class DadoTemporalController {

    private final DadoTemporalService service;

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<DadoTemporalResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id).toEntityModel());
    }

    @GetMapping("/talhao/{idTalhao}")
    public ResponseEntity<CollectionModel<EntityModel<DadoTemporalResponse>>> findByTalhao(@PathVariable Long idTalhao) {
        List<EntityModel<DadoTemporalResponse>> models = service.findByTalhao(idTalhao).stream()
                .map(DadoTemporalResponse::toEntityModel)
                .collect(Collectors.toList());
        
        var linkSelf = linkTo(methodOn(DadoTemporalController.class).findByTalhao(idTalhao)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(models, linkSelf));
    }

    @GetMapping("/req-api/{idReqApi}")
    public ResponseEntity<CollectionModel<EntityModel<DadoTemporalResponse>>> findByReqApi(@PathVariable Long idReqApi) {
        List<EntityModel<DadoTemporalResponse>> models = service.findByReqApi(idReqApi).stream()
                .map(DadoTemporalResponse::toEntityModel)
                .collect(Collectors.toList());
                
        var linkSelf = linkTo(methodOn(DadoTemporalController.class).findByReqApi(idReqApi)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(models, linkSelf));
    }
}
