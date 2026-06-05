package fiap.com.br.terranova.validation;

import fiap.com.br.terranova.propriedade.dto.PropriedadeRequest;
import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.talhao.TalhaoRepository;
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
public class ValidPropriedadeAreaValidator implements ConstraintValidator<ValidPropriedadeArea, PropriedadeRequest> {

    private final TalhaoRepository talhaoRepository;
    private final HttpServletRequest request;

    @Override
    public boolean isValid(PropriedadeRequest dto, ConstraintValidatorContext context) {
        if (dto == null || dto.tamanhoTotal() == null) {
            return true;
        }

        Long propriedadeId = null;
        Object attr = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (attr instanceof Map) {
            Map<?, ?> pathVariables = (Map<?, ?>) attr;
            if (pathVariables.containsKey("id")) {
                try {
                    propriedadeId = Long.parseLong(String.valueOf(pathVariables.get("id")));
                } catch (NumberFormatException ignored) {}
            }
        }

        if (propriedadeId == null) {
            return true; // Criação de propriedade: ainda não possui talhões
        }

        double totalTalhoesArea = talhaoRepository.findByPropriedadeIdPropriedade(propriedadeId)
                .stream()
                .map(Talhao::getVolumArea)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        return dto.tamanhoTotal() >= totalTalhoesArea;
    }
}
