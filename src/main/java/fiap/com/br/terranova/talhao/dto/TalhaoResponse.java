package fiap.com.br.terranova.talhao.dto;

import fiap.com.br.terranova.talhao.Talhao;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import fiap.com.br.terranova.talhao.TalhaoController;

import java.math.BigDecimal;

public record TalhaoResponse(
        Long id,
        String nomeTalhao,
        BigDecimal volumArea,
        Long idTipoPlantacao,
        String tipoPlantacaoNome,
        Long idPropriedade,
        String propriedadeNome,
        Long idLocalizacao
) {
    public static TalhaoResponse fromEntity(Talhao entity) {
        return new TalhaoResponse(
                entity.getIdTalhao(),
                entity.getNomeTalhao(),
                entity.getVolumArea(),
                entity.getTipoPlantacao().getIdTipoPlant(),
                entity.getTipoPlantacao().getTipoPlant(),
                entity.getPropriedade().getIdPropriedade(),
                entity.getPropriedade().getNome(),
                entity.getLocalizacao().getIdLocalizacao()
        );
    }

    public EntityModel<TalhaoResponse> toEntityModel() {
        var linkSelf = linkTo(methodOn(TalhaoController.class).findById(id)).withSelfRel().withTitle("Detalhes do talhao");
        var linkAll = linkTo(methodOn(TalhaoController.class).findAll(null)).withRel("all-talhoes").withTitle("Todos os talhoes");
        return EntityModel.of(this, linkSelf, linkAll);
    }
}
