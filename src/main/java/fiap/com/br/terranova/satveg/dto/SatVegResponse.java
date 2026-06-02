package fiap.com.br.terranova.satveg.dto;

import fiap.com.br.terranova.satveg.SatVeg;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import fiap.com.br.terranova.satveg.SatVegController;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonRawValue;

public record SatVegResponse(
        Long id,
        String tipoPerfil,
        Timestamp dataAnalise,

        @JsonRawValue
        String dadosJson,
        
        Long idTalhao
) {
    public static SatVegResponse fromEntity(SatVeg entity) {
        return new SatVegResponse(
                entity.getIdSatveg(),
                entity.getTipoPerfil(),
                entity.getDataAnalise(),
                entity.getDadosJson(),
                entity.getTalhao().getIdTalhao()
        );
    }

    public EntityModel<SatVegResponse> toEntityModel() {
        var linkSelf = linkTo(methodOn(SatVegController.class).findById(id)).withSelfRel().withTitle("Detalhes da analise SATveg");
        var linkAll = linkTo(methodOn(SatVegController.class).findAll(null)).withRel("all-satveg").withTitle("Todas as analises SATveg");
        return EntityModel.of(this, linkSelf, linkAll);
    }
}