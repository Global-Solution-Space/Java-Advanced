package fiap.com.br.terranova.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueTelefoneValidator.class)
public @interface UniqueTelefone {
    String message() default "O telefone informado ja esta em uso no sistema.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
