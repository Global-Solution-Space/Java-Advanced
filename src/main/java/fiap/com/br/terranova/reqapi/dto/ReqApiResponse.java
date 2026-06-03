package fiap.com.br.terranova.reqapi.dto;

import fiap.com.br.terranova.reqapi.ReqApi;
import fiap.com.br.terranova.reqapi.ReqApiController;
import org.springframework.hateoas.EntityModel;

import java.sql.Timestamp;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public record ReqApiResponse(
        Long id,
        String tipoParam,
        Timestamp dataAnalise,
        Long idTipoApi,
        String tipoApiNome,
        int totalDados
) {
    public static ReqApiResponse fromEntity(ReqApi entity) {
        return new ReqApiResponse(
                entity.getIdApi(),
                entity.getTipoParam(),
                entity.getDataAnalise(),
                entity.getTipoApi().getIdTipo(),
                entity.getTipoApi().getTipoApi(),
                entity.getDados() != null ? entity.getDados().size() : 0
        );
    }

    public EntityModel<ReqApiResponse> toEntityModel() {
        var linkSelf = linkTo(methodOn(ReqApiController.class).findById(id)).withSelfRel().withTitle("Detalhes da requisicao");
        var linkAll = linkTo(methodOn(ReqApiController.class).findAll(null)).withRel("all-req-api").withTitle("Todas as requisicoes");
        return EntityModel.of(this, linkSelf, linkAll);
    }
}
