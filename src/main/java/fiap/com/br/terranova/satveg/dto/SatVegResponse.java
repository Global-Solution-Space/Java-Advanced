package fiap.com.br.terranova.satveg.dto;

import fiap.com.br.terranova.satveg.SatVeg;
import fiap.com.br.terranova.satveg.SatVegController;
import org.springframework.hateoas.EntityModel;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public record SatVegResponse(
        Long id,
        String tipoPerfil,
        Timestamp dataAnalise,
        Map<LocalDate, Double> dados,
        Long idTalhao
) {
    public static SatVegResponse fromEntity(SatVeg entity) {
        Map<LocalDate, Double> dadosMap = new LinkedHashMap<>();
        if (entity.getDados() != null) {
            entity.getDados().forEach(d -> dadosMap.put(d.getDataLeitura(), d.getValor()));
        }

        return new SatVegResponse(
                entity.getIdSatveg(),
                entity.getTipoPerfil(),
                entity.getDataAnalise(),
                dadosMap,
                entity.getTalhao().getIdTalhao()
        );
    }

    public EntityModel<SatVegResponse> toEntityModel() {
        var linkSelf = linkTo(methodOn(SatVegController.class).findById(id)).withSelfRel().withTitle("Detalhes da analise SATveg");
        var linkAll = linkTo(methodOn(SatVegController.class).findAll(null)).withRel("all-satveg").withTitle("Todas as analises SATveg");
        return EntityModel.of(this, linkSelf, linkAll);
    }
}