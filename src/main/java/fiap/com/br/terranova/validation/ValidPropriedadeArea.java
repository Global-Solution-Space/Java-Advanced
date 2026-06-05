package fiap.com.br.terranova.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPropriedadeAreaValidator.class)
public @interface ValidPropriedadeArea {
    String message() default "O tamanho da propriedade não pode ser menor do que a soma das áreas de seus talhões existentes.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
