package fiap.com.br.terranova.reqapi;

import fiap.com.br.terranova.dadotemporal.DadoTemporal;
import fiap.com.br.terranova.reqapi.dto.ReqApiResponse;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApi;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReqApiResponseTest {

    @Test
    void shouldMapReqApiEntityToResponseWithTotalDados() {
        Timestamp dataAnalise = Timestamp.valueOf("2026-06-05 10:30:00");
        TipoApi tipoApi = TipoApi.builder()
                .idTipo(2L)
                .tipoApi("SATVEG")
                .build();
        ReqApi reqApi = ReqApi.builder()
                .idApi(10L)
                .tipoParam("NDVI")
                .dataAnalise(dataAnalise)
                .tipoApi(tipoApi)
                .dados(List.of(
                        DadoTemporal.builder().idDado(1L).build(),
                        DadoTemporal.builder().idDado(2L).build()
                ))
                .build();

        ReqApiResponse response = ReqApiResponse.fromEntity(reqApi);

        assertEquals(10L, response.id());
        assertEquals("SATVEG", response.tipoApiNome());
        assertEquals("NDVI", response.tipoParam());
        assertEquals(dataAnalise, response.dataAnalise());
        assertEquals(2L, response.idTipoApi());
        assertEquals(2, response.totalDados());
    }

    @Test
    void shouldMapNullDadosAsZeroTotalDados() {
        ReqApi reqApi = ReqApi.builder()
                .idApi(11L)
                .tipoParam("PRECTOTCORR")
                .dataAnalise(Timestamp.valueOf("2026-06-05 11:00:00"))
                .tipoApi(TipoApi.builder().idTipo(1L).tipoApi("NASAPOWER").build())
                .dados(null)
                .build();

        ReqApiResponse response = ReqApiResponse.fromEntity(reqApi);

        assertEquals(11L, response.id());
        assertEquals("NASAPOWER", response.tipoApiNome());
        assertEquals(0, response.totalDados());
    }
}
