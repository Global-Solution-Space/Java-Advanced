package fiap.com.br.terranova.validation;

import fiap.com.br.terranova.telefone.TelefoneRepository;
import fiap.com.br.terranova.telefone.dto.TelefoneRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

@Component
@NoArgsConstructor
public class UniqueTelefoneValidator implements ConstraintValidator<UniqueTelefone, TelefoneRequest> {

    @Setter
    @Autowired
    private TelefoneRepository telefoneRepository;

    @Setter
    @Autowired
    private HttpServletRequest request;

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
                    // Se for atualização, ignora o próprio telefone
                    return true;
                } catch (NumberFormatException ignored) {}
            }
        }

        return !telefoneRepository.existsByDddAndNumero(telefoneRequest.ddd(), telefoneRequest.numero());
    }
}
