package fiap.com.br.terranova.propriedade.dto;

import fiap.com.br.terranova.propriedade.Propriedade;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import fiap.com.br.terranova.propriedade.PropriedadeController;

import java.math.BigDecimal;

public record PropriedadeResponse(
        Long id,
        String nome,
        BigDecimal tamanhoTotal,
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
        return EntityModel.of(this, linkSelf, linkAll);
    }
}
