package fiap.com.br.terranova.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidLocalizacaoDisponibilidadeValidator.class)
public @interface ValidLocalizacaoDisponivel {
    String message() default "Esta localização já está vinculada a outra propriedade ou talhão.";

    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
