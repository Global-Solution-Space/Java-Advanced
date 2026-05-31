package fiap.com.br.terranova.integration.satveg;

import java.util.List;

public record SatVegDataResponse(
        List<Double> listaSerie,
        List<String> listaDatas
) {}
