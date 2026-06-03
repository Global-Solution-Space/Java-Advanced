package fiap.com.br.terranova.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidTalhaoAreaValidator.class)
public @interface ValidTalhaoArea {
    String message() default "A soma das areas dos talhoes excede o tamanho total da propriedade.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
