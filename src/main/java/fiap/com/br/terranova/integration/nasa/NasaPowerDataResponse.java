package fiap.com.br.terranova.integration.nasa;

import java.util.List;
import java.util.Map;

public record NasaPowerDataResponse(
        Geometry geometry,
        Properties properties
) {
    public record Geometry(List<Double> coordinates) {}
    public record Properties(
            Map<String, Map<String, Double>> parameter
    ) {}
}
