package fiap.com.br.terranova.nasapower.dto;

import fiap.com.br.terranova.nasapower.NasaPower;
import fiap.com.br.terranova.nasapower.NasaPowerController;
import org.springframework.hateoas.EntityModel;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public record NasaPowerResponse(
        Long id,
        LocalDate dataInicio,
        LocalDate dataFim,
        String parametro,
        Timestamp dataAnalise,
        Map<LocalDate, Double> dados,
        Long idTalhao
) {
    public static NasaPowerResponse fromEntity(NasaPower entity) {
        Map<LocalDate, Double> dadosMap = new LinkedHashMap<>();
        if (entity.getDados() != null) {
            entity.getDados().forEach(d -> dadosMap.put(d.getDataLeitura(), d.getValor()));
        }

        return new NasaPowerResponse(
                entity.getIdNasapower(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getParametro(),
                entity.getDataAnalise(),
                dadosMap,
                entity.getTalhao().getIdTalhao()
        );
    }

    public EntityModel<NasaPowerResponse> toEntityModel() {
        var linkSelf = linkTo(methodOn(NasaPowerController.class).findById(id)).withSelfRel().withTitle("Detalhes da analise da NASA");
        var linkAll = linkTo(methodOn(NasaPowerController.class).findAll(null)).withRel("all-nasapower").withTitle("Todas as analises");
        return EntityModel.of(this, linkSelf, linkAll);
    }
}