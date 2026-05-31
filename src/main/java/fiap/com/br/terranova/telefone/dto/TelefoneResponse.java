package fiap.com.br.terranova.telefone.dto;

import fiap.com.br.terranova.telefone.Telefone;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import fiap.com.br.terranova.telefone.TelefoneController;

public record TelefoneResponse(
        Long id,
        String ddd,
        String numero,
        Long idProdutor
) {
    public static TelefoneResponse fromEntity(Telefone entity) {
        return new TelefoneResponse(
                entity.getIdTelefone(),
                entity.getDdd(),
                entity.getNumero(),
                entity.getProdutor().getIdProdutor()
        );
    }

    public EntityModel<TelefoneResponse> toEntityModel() {
        var linkSelf = linkTo(methodOn(TelefoneController.class).findById(id)).withSelfRel().withTitle("Detalhes do telefone");
        var linkAll = linkTo(methodOn(TelefoneController.class).findAll(null)).withRel("all-telefones").withTitle("Todos os telefones");
        return EntityModel.of(this, linkSelf, linkAll);
    }
}
