package fiap.com.br.terranova.validation;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;
import java.util.Map;
import fiap.com.br.terranova.produtor.Produtor;
import fiap.com.br.terranova.produtor.ProdutorRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final ProdutorRepository produtorRepository;
    private final HttpServletRequest request;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true;
        }

        Object attr = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (attr instanceof Map) {
            Map<?, ?> pathVariables = (Map<?, ?>) attr;
            if (pathVariables.containsKey("id")) {
                try {
                    Long id = Long.parseLong(String.valueOf(pathVariables.get("id")));
                    Produtor existing = produtorRepository.findById(id).orElse(null);
                    if (existing != null && existing.getEmail().equalsIgnoreCase(email)) {
                        return true; // O e-mail pertence ao proprio produtor sendo atualizado
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        
        return !produtorRepository.existsByEmail(email);
    }
}
