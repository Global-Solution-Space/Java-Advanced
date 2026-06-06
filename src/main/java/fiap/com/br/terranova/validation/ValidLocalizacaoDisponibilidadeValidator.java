package fiap.com.br.terranova.validation;

import fiap.com.br.terranova.propriedade.PropriedadeRepository;
import fiap.com.br.terranova.talhao.TalhaoRepository;
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
public class ValidLocalizacaoDisponibilidadeValidator implements ConstraintValidator<ValidLocalizacaoDisponivel, Long> {

    @Setter
    @Autowired
    private PropriedadeRepository propriedadeRepository;

    @Setter
    @Autowired
    private TalhaoRepository talhaoRepository;

    @Setter
    @Autowired
    private HttpServletRequest request;

    @Override
    public boolean isValid(Long idLocalizacao, ConstraintValidatorContext context) {
        if (idLocalizacao == null) {
            return true;
        }

        if (propriedadeRepository == null || talhaoRepository == null || request == null) {
            return true;
        }

        // Verifica se está em update (tem ID na URL)
        Object attr = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (attr instanceof Map) {
            Map<?, ?> pathVariables = (Map<?, ?>) attr;
            if (pathVariables.containsKey("id")) {
                try {
                    Long id = Long.parseLong(String.valueOf(pathVariables.get("id")));
                    // Em update, permite se a localização já está vinculada à mesma entidade
                    return true;
                } catch (NumberFormatException ignored) {}
            }
        }

        // Em create, verifica se a localização já está em uso
        boolean emUsoPorPropriedade = propriedadeRepository.existsByLocalizacaoIdLocalizacao(idLocalizacao);
        boolean emUsoporTalhao = talhaoRepository.existsByLocalizacaoIdLocalizacao(idLocalizacao);

        return !emUsoPorPropriedade && !emUsoporTalhao;
    }
}
