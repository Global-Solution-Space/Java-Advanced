package fiap.com.br.terranova.validation;

import fiap.com.br.terranova.telefone.Telefone;
import fiap.com.br.terranova.telefone.TelefoneRepository;
import fiap.com.br.terranova.telefone.dto.TelefoneRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UniqueTelefoneValidator implements ConstraintValidator<UniqueTelefone, TelefoneRequest> {

    private final TelefoneRepository telefoneRepository;
    private final HttpServletRequest request;

    @Override
    public boolean isValid(TelefoneRequest telefoneRequest, ConstraintValidatorContext context) {
        if (telefoneRequest == null || telefoneRequest.ddd() == null || telefoneRequest.numero() == null) {
            return true;
        }

        if (telefoneRepository == null || request == null) {
            return true;
        }

        Object attr = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (attr instanceof Map) {
            Map<?, ?> pathVariables = (Map<?, ?>) attr;
            if (pathVariables.containsKey("id")) {
                try {
                    Long id = Long.parseLong(String.valueOf(pathVariables.get("id")));
                    Telefone existing = telefoneRepository.findById(id).orElse(null);
                    if (existing != null && existing.getDdd().equals(telefoneRequest.ddd()) && existing.getNumero().equals(telefoneRequest.numero())) {
                        return true; // O telefone pertence ao próprio registro sendo atualizado
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        return !telefoneRepository.existsByDddAndNumero(telefoneRequest.ddd(), telefoneRequest.numero());
    }
}
