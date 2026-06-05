package fiap.com.br.terranova.produtor.dto;

import fiap.com.br.terranova.produtor.Produtor;
import fiap.com.br.terranova.alerta.AlertaController;
import fiap.com.br.terranova.propriedade.PropriedadeController;
import fiap.com.br.terranova.talhao.TalhaoController;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import fiap.com.br.terranova.produtor.ProdutorController;

public record ProdutorResponse(
        Long id,
        String nome,
        String email,
        String senha
) {
    public static ProdutorResponse fromEntity(Produtor produtor) {
        return new ProdutorResponse(
                produtor.getIdProdutor(),
                produtor.getNome(),
                produtor.getEmail(),
                produtor.getSenha()
        );
    }

    public EntityModel<ProdutorResponse> toEntityModel() {
        var linkSelf = linkTo(methodOn(ProdutorController.class).findById(id)).withSelfRel().withTitle("Detalhes do produtor");
        var linkAll = linkTo(methodOn(ProdutorController.class).findAll(null)).withRel("all-produtores").withTitle("Todos os produtores");
        var linkPropriedades = linkTo(methodOn(PropriedadeController.class).findByProdutor(id)).withRel("propriedades").withTitle("Propriedades do produtor");
        var linkTalhoes = linkTo(methodOn(TalhaoController.class).findByProdutor(id)).withRel("talhoes").withTitle("Talhões do produtor");
        var linkAlertas = linkTo(methodOn(AlertaController.class).findByProdutor(id)).withRel("alertas").withTitle("Alertas do produtor");
        return EntityModel.of(this, linkSelf, linkAll, linkPropriedades, linkTalhoes, linkAlertas);
    }
}
