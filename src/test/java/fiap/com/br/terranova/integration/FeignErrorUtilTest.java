package fiap.com.br.terranova.integration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeignErrorUtilTest {

    @Test
    void shouldExtractDetailFromJsonPayload() {
        String detail = FeignErrorUtil.extractDetail("{\"detail\":\"Coordenada invalida\",\"status\":400}");

        assertEquals("Coordenada invalida", detail);
    }

    @Test
    void shouldReturnFallbackWhenDetailIsMissingOrPayloadInvalid() {
        assertEquals("Coordenadas ou parametros invalidos.", FeignErrorUtil.extractDetail("{\"message\":\"erro\"}"));
        assertEquals("Coordenadas ou parametros invalidos.", FeignErrorUtil.extractDetail(null));
    }
}
