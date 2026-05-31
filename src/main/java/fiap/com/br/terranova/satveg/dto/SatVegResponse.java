package fiap.com.br.terranova.satveg.dto;

import fiap.com.br.terranova.satveg.SatVeg;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import fiap.com.br.terranova.satveg.SatVegController;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record SatVegResponse(
        Long id,
        BigDecimal tipoPerfil,
        BigDecimal satelite,
        Integer preFiltro,
        String filtro,
        Integer parametroFiltro,
        Timestamp dataAnalise,
        Long idTalhao
) {
    public static SatVegResponse fromEntity(SatVeg entity) {
        return new SatVegResponse(
                entity.getIdSatveg(),
                entity.getTipoPerfil(),
                entity.getSatelite(),
                entity.getPreFiltro(),
                entity.getFiltro(),
                entity.getParametroFiltro(),
                entity.getDataAnalise(),
                entity.getTalhao().getIdTalhao()
        );
    }

    public EntityModel<SatVegResponse> toEntityModel() {
        var linkSelf = linkTo(methodOn(SatVegController.class).findById(id)).withSelfRel().withTitle("Detalhes da analise SATveg");
        var linkAll = linkTo(methodOn(SatVegController.class).findAll(null)).withRel("all-satveg").withTitle("Todas as analises SATveg");
        return EntityModel.of(this, linkSelf, linkAll);
    }
}