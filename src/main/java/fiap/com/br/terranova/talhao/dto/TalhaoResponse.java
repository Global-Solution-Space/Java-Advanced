package fiap.com.br.terranova.talhao.dto;

import fiap.com.br.terranova.propriedade.PropriedadeController;
import fiap.com.br.terranova.localizacao.LocalizacaoController;
import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.tipoplantacao.TipoPlantacaoController;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import fiap.com.br.terranova.talhao.TalhaoController;

public record TalhaoResponse(
        Long id,
        String nomeTalhao,
        Double volumArea,
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
        var linkPropriedade = linkTo(methodOn(PropriedadeController.class).findById(idPropriedade)).withRel("propriedade").withTitle("Propriedade associada");
        var linkTipoPlantacao = linkTo(methodOn(TipoPlantacaoController.class).findById(idTipoPlantacao)).withRel("tipo-plantacao").withTitle("Tipo de plantação associado");
        var linkLocalizacao = linkTo(methodOn(LocalizacaoController.class).findById(idLocalizacao)).withRel("localizacao").withTitle("Localização associada");
        return EntityModel.of(this, linkSelf, linkAll, linkPropriedade, linkTipoPlantacao, linkLocalizacao);
    }
}
