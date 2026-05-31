package fiap.com.br.terranova.nasapower.dto;

import fiap.com.br.terranova.nasapower.NasaPower;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import fiap.com.br.terranova.nasapower.NasaPowerController;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record NasaPowerResponse(
        Long id,
        String dataInicio,
        String dataFim,
        BigDecimal elevacao,
        Timestamp dataAnalise,
        Long idTalhao
) {
    public static NasaPowerResponse fromEntity(NasaPower entity) {
        return new NasaPowerResponse(
                entity.getIdNasapower(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getElevacao(),
                entity.getDataAnalise(),
                entity.getTalhao().getIdTalhao()
        );
    }

    public EntityModel<NasaPowerResponse> toEntityModel() {
        var linkSelf = linkTo(methodOn(NasaPowerController.class).findById(id)).withSelfRel().withTitle("Detalhes da analise da NASA");
        var linkAll = linkTo(methodOn(NasaPowerController.class).findAll(null)).withRel("all-nasapower").withTitle("Todas as analises");
        return EntityModel.of(this, linkSelf, linkAll);
    }
}