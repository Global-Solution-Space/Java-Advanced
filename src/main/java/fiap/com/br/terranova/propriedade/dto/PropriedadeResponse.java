package fiap.com.br.terranova.propriedade.dto;

import fiap.com.br.terranova.localizacao.LocalizacaoController;
import fiap.com.br.terranova.produtor.ProdutorController;
import fiap.com.br.terranova.propriedade.Propriedade;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import fiap.com.br.terranova.propriedade.PropriedadeController;

public record PropriedadeResponse(
        Long id,
        String nome,
        Double tamanhoTotal,
        Long idProdutor,
        String produtorNome,
        Long idLocalizacao
) {
    public static PropriedadeResponse fromEntity(Propriedade entity) {
        return new PropriedadeResponse(
                entity.getIdPropriedade(),
                entity.getNome(),
                entity.getTamanhoTotal(),
                entity.getProdutor().getIdProdutor(),
                entity.getProdutor().getNome(),
                entity.getLocalizacao().getIdLocalizacao()
        );
    }

    public EntityModel<PropriedadeResponse> toEntityModel() {
        var linkSelf = linkTo(methodOn(PropriedadeController.class).findById(id)).withSelfRel().withTitle("Detalhes da propriedade");
        var linkAll = linkTo(methodOn(PropriedadeController.class).findAll(null)).withRel("all-propriedades").withTitle("Todas as propriedades");
        var linkProdutor = linkTo(methodOn(ProdutorController.class).findById(idProdutor)).withRel("produtor").withTitle("Produtor associado");
        var linkLocalizacao = linkTo(methodOn(LocalizacaoController.class).findById(idLocalizacao)).withRel("localizacao").withTitle("Localização associada");
        return EntityModel.of(this, linkSelf, linkAll, linkProdutor, linkLocalizacao);
    }
}
