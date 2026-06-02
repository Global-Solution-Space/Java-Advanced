package fiap.com.br.terranova.nasapower.dto;

import fiap.com.br.terranova.nasapower.NasaPower;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import fiap.com.br.terranova.nasapower.NasaPowerController;

import java.sql.Timestamp;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonRawValue;

public record NasaPowerResponse(
        Long id,
        LocalDate dataInicio,
        LocalDate dataFim,
        String parametro,
        Timestamp dataAnalise,
        
        @JsonRawValue
        String dadosJson,
        
        Long idTalhao
) {
    public static NasaPowerResponse fromEntity(NasaPower entity) {
        return new NasaPowerResponse(
                entity.getIdNasapower(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getParametro(),
                entity.getDataAnalise(),
                entity.getDadosJson(),
                entity.getTalhao().getIdTalhao()
        );
    }

    public EntityModel<NasaPowerResponse> toEntityModel() {
        var linkSelf = linkTo(methodOn(NasaPowerController.class).findById(id)).withSelfRel().withTitle("Detalhes da analise da NASA");
        var linkAll = linkTo(methodOn(NasaPowerController.class).findAll(null)).withRel("all-nasapower").withTitle("Todas as analises");
        return EntityModel.of(this, linkSelf, linkAll);
    }
}