package fiap.com.br.terranova.validation;

import fiap.com.br.terranova.propriedade.Propriedade;
import fiap.com.br.terranova.propriedade.PropriedadeRepository;
import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.talhao.TalhaoRepository;
import fiap.com.br.terranova.talhao.dto.TalhaoRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ValidTalhaoAreaValidator implements ConstraintValidator<ValidTalhaoArea, TalhaoRequest> {

    private final TalhaoRepository talhaoRepository;
    private final PropriedadeRepository propriedadeRepository;
    private final HttpServletRequest request;

    @Override
    public boolean isValid(TalhaoRequest dto, ConstraintValidatorContext context) {
        if (dto == null || dto.idPropriedade() == null || dto.volumArea() == null) {
            return true;
        }

        Propriedade propriedade = propriedadeRepository.findById(dto.idPropriedade()).orElse(null);
        if (propriedade == null) {
            return true;
        }

        Long talhaoIdExcluido = null;
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables != null && pathVariables.containsKey("id")) {
            try {
                talhaoIdExcluido = Long.parseLong(pathVariables.get("id"));
            } catch (NumberFormatException ignored) {}
        }

        final Long finalIdExcluido = talhaoIdExcluido;
        double totalTalhoesArea = talhaoRepository.findByPropriedadeIdPropriedade(propriedade.getIdPropriedade())
                .stream()
                .filter(t -> finalIdExcluido == null || !t.getIdTalhao().equals(finalIdExcluido))
                .map(Talhao::getVolumArea)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        double novoTotal = totalTalhoesArea + dto.volumArea();

        return novoTotal <= propriedade.getTamanhoTotal();
    }
}
