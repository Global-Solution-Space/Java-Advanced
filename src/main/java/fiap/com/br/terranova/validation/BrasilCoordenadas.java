package fiap.com.br.terranova.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = BrasilCoordenadasValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BrasilCoordenadas {
    String message() default "As coordenadas informadas não pertencem ao Brasil (Localização: Oceano/Desconhecido).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
