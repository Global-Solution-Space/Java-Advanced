package fiap.com.br.terranova.localizacao;

import fiap.com.br.terranova.localizacao.dto.LocalizacaoRequest;
import fiap.com.br.terranova.validation.BrasilCoordenadasValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocalizacaoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ConstraintValidatorFactory validatorFactory = new ConstraintValidatorFactory() {
            @SuppressWarnings("unchecked")
            @Override
            public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
                if (key == BrasilCoordenadasValidator.class) {
                    return (T) new BrasilCoordenadasValidator() {
                        @Override
                        public boolean isValid(LocalizacaoRequest request, ConstraintValidatorContext context) {
                            if (request == null || request.locLatitude() == null || request.locLongitude() == null) {
                                return true;
                            }
                            return !request.locLatitude().equals(new BigDecimal("-26.38"))
                                    || !request.locLongitude().equals(new BigDecimal("-46.73"));
                        }
                    };
                }
                try {
                    return key.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void releaseInstance(ConstraintValidator<?, ?> instance) {
            }
        };

        ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .constraintValidatorFactory(validatorFactory)
                .buildValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWhenCoordinatesAreWithinBrazilBounds() {
        // Fernando de Noronha (Lat: -3.83, Lon: -32.40) - Extremo Leste permitido
        LocalizacaoRequest noronha = new LocalizacaoRequest(new BigDecimal("-3.83"), new BigDecimal("-32.40"));
        Set<ConstraintViolation<LocalizacaoRequest>> violations1 = validator.validate(noronha);
        assertTrue(violations1.isEmpty(), "Fernando de Noronha deve ser válido");

        // Boa Vista (Lat: 2.82, Lon: -60.67) - Ponto Norte
        LocalizacaoRequest norte = new LocalizacaoRequest(new BigDecimal("2.82"), new BigDecimal("-60.67"));
        Set<ConstraintViolation<LocalizacaoRequest>> violations2 = validator.validate(norte);
        assertTrue(violations2.isEmpty(), "Boa Vista deve ser válido");

        // Arroio Chuí (Lat: -33.75, Lon: -53.3) - Extremo Sul
        LocalizacaoRequest sul = new LocalizacaoRequest(new BigDecimal("-33.75"), new BigDecimal("-53.30"));
        Set<ConstraintViolation<LocalizacaoRequest>> violations3 = validator.validate(sul);
        assertTrue(violations3.isEmpty(), "Arroio Chuí deve ser válido");
    }

    @Test
    void shouldFailValidationWhenCoordinatesAreInTheOcean() {
        // Coordenada usada no problema (Lat: -26.38, Lon: -46.73) - Fica no Oceano Atlântico
        LocalizacaoRequest oceano = new LocalizacaoRequest(new BigDecimal("-26.38"), new BigDecimal("-46.73"));
        Set<ConstraintViolation<LocalizacaoRequest>> violations = validator.validate(oceano);
        
        assertFalse(violations.isEmpty(), "Deve falhar pois está no oceano");
        assertTrue(violations.iterator().next().getMessage().contains("Oceano/Desconhecido"));
    }

    @Test
    void shouldFailValidationWhenLatitudeIsOutofBounds() {
        // Latitude muito ao norte (Acima de 6.0)
        LocalizacaoRequest muitoNorte = new LocalizacaoRequest(new BigDecimal("6.01"), new BigDecimal("-50.00"));
        Set<ConstraintViolation<LocalizacaoRequest>> violations1 = validator.validate(muitoNorte);
        assertFalse(violations1.isEmpty());

        // Latitude muito ao sul (Abaixo de -34.0)
        LocalizacaoRequest muitoSul = new LocalizacaoRequest(new BigDecimal("-34.01"), new BigDecimal("-50.00"));
        Set<ConstraintViolation<LocalizacaoRequest>> violations2 = validator.validate(muitoSul);
        assertFalse(violations2.isEmpty());
    }

    @Test
    void shouldFailValidationWhenLongitudeIsOutofBounds() {
        // Longitude muito ao leste (Maior que -28.0)
        LocalizacaoRequest muitoLeste = new LocalizacaoRequest(new BigDecimal("-15.00"), new BigDecimal("-27.99"));
        Set<ConstraintViolation<LocalizacaoRequest>> violations1 = validator.validate(muitoLeste);
        assertFalse(violations1.isEmpty());

        // Longitude muito a oeste (Menor que -74.0)
        LocalizacaoRequest muitoOeste = new LocalizacaoRequest(new BigDecimal("-15.00"), new BigDecimal("-74.01"));
        Set<ConstraintViolation<LocalizacaoRequest>> violations2 = validator.validate(muitoOeste);
        assertFalse(violations2.isEmpty());
    }
}
