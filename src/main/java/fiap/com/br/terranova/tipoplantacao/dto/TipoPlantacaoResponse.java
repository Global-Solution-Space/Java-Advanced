package fiap.com.br.terranova.tipoplantacao.dto;

import fiap.com.br.terranova.tipoplantacao.TipoPlantacao;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import fiap.com.br.terranova.tipoplantacao.TipoPlantacaoController;

public record TipoPlantacaoResponse(
        Long id,
        String tipoPlant
) {
    public static TipoPlantacaoResponse fromEntity(TipoPlantacao entity) {
        return new TipoPlantacaoResponse(
                entity.getIdTipoPlant(),
                entity.getTipoPlant()
        );
    }

    public EntityModel<TipoPlantacaoResponse> toEntityModel() {
        var linkSelf = linkTo(methodOn(TipoPlantacaoController.class).findById(id)).withSelfRel().withTitle("Detalhes do tipo de plantacao");
        var linkAll = linkTo(methodOn(TipoPlantacaoController.class).findAll(null)).withRel("all-tipos-plantacao").withTitle("Todos os tipos de plantacao");
        return EntityModel.of(this, linkSelf, linkAll);
    }
}