package fiap.com.br.terranova.localizacao.dto;

import fiap.com.br.terranova.localizacao.Localizacao;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import fiap.com.br.terranova.localizacao.LocalizacaoController;

import java.math.BigDecimal;

public record LocalizacaoResponse(
        Long id,
        BigDecimal locLatitude,
        BigDecimal locLongitude
) {
    public static LocalizacaoResponse fromEntity(Localizacao entity) {
        return new LocalizacaoResponse(
                entity.getIdLocalizacao(),
                BigDecimal.valueOf(entity.getCoordenadas().getY()),
                BigDecimal.valueOf(entity.getCoordenadas().getX())
        );
    }

    public EntityModel<LocalizacaoResponse> toEntityModel() {
        var linkSelf = linkTo(methodOn(LocalizacaoController.class).findById(id)).withSelfRel().withTitle("Detalhes da localizacao");
        var linkAll = linkTo(methodOn(LocalizacaoController.class).findAll(null)).withRel("all-localizacoes").withTitle("Todas as localizacoes");
        return EntityModel.of(this, linkSelf, linkAll);
    }
}