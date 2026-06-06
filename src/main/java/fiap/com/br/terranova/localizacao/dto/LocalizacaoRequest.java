package fiap.com.br.terranova.localizacao.dto;

import fiap.com.br.terranova.localizacao.Localizacao;
import fiap.com.br.terranova.validation.BrasilCoordenadas;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import org.locationtech.jts.geom.Point;

@BrasilCoordenadas
public record LocalizacaoRequest(
        @NotNull
        @DecimalMin(value = "-34.00", message = "A latitude deve ser no mínimo -34.00")
        @DecimalMax(value = "6.00", message = "A latitude deve ser no máximo 6.00")
        BigDecimal locLatitude,

        @NotNull
        @DecimalMin(value = "-74.00", message = "A longitude deve ser no mínimo -74.00")
        @DecimalMax(value = "-28.00", message = "A longitude deve ser no máximo -28.00")
        BigDecimal locLongitude
) {
    public Localizacao toEntity() {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
        Point point = factory.createPoint(new Coordinate(locLongitude.doubleValue(), locLatitude.doubleValue()));

        return Localizacao.builder()
                .coordenadas(point)
                .build();
    }
}