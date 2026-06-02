package fiap.com.br.terranova.alerta.dto;

import fiap.com.br.terranova.alerta.Alerta;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import fiap.com.br.terranova.alerta.AlertaController;

import java.sql.Timestamp;

public record AlertaResponse(
        Long id,
        String titulo,
        String descricao,
        String nivelAlerta,
        String resolvido,
        Timestamp dataAlerta,
        Long idTalhao
) {
    public static AlertaResponse fromEntity(Alerta entity) {
        return new AlertaResponse(
                entity.getIdAlerta(),
                entity.getTitulo(),
                entity.getDescricao(),
                entity.getNivelAlerta(),
                entity.getResolvido(),
                entity.getDataAlerta(),
                entity.getTalhao().getIdTalhao()
        );
    }

    public EntityModel<AlertaResponse> toEntityModel() {
        var linkSelf = linkTo(methodOn(AlertaController.class).findById(id)).withSelfRel().withTitle("Detalhes do alerta");
        var linkAll = linkTo(methodOn(AlertaController.class).findAll(null)).withRel("all-alertas").withTitle("Todos os alertas");
        return EntityModel.of(this, linkSelf, linkAll);
    }
}