package fiap.com.br.terranova.dadotemporal.dto;

import fiap.com.br.terranova.dadotemporal.DadoTemporal;
import fiap.com.br.terranova.dadotemporal.DadoTemporalController;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDate;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public record DadoTemporalResponse(
        Long idDado,
        LocalDate dataLeitura,
        Double valor,
        Long idTalhao,
        Long idReqApi,
        String tipoApiNome,
        String tipoParam
) {
    public static DadoTemporalResponse fromEntity(DadoTemporal entity) {
        return new DadoTemporalResponse(
                entity.getIdDado(),
                entity.getDataLeitura(),
                entity.getValor(),
                entity.getTalhao().getIdTalhao(),
                entity.getReqApi().getIdApi(),
                entity.getReqApi().getTipoApi().getTipoApi(),
                entity.getReqApi().getTipoParam()
        );
    }

    public EntityModel<DadoTemporalResponse> toEntityModel() {
        var linkSelf = linkTo(methodOn(DadoTemporalController.class).findById(idDado)).withSelfRel().withTitle("Detalhes do dado temporal");
        var linkTalhao = linkTo(methodOn(DadoTemporalController.class).findByTalhao(idTalhao, null)).withRel("dados-talhao").withTitle("Todos os dados do talhão");
        var linkReqApi = linkTo(methodOn(DadoTemporalController.class).findByReqApi(idReqApi, null)).withRel("dados-reqapi").withTitle("Todos os dados desta requisição");
        return EntityModel.of(this, linkSelf, linkTalhao, linkReqApi);
    }
}
