package fiap.com.br.terranova.reqapi.dto;

import fiap.com.br.terranova.reqapi.ReqApi;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApiEnum;
import fiap.com.br.terranova.reqapi.TipoParam;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApi;
import fiap.com.br.terranova.validation.EnumValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.sql.Timestamp;

public record ReqApiRequest(
        @NotBlank
        @Size(max = 15)
        @EnumValidation(enumClass = TipoParam.class, message = "O tipo de parametro deve ser: NDVI ou PRECTOTCORR")
        String tipoParam,

        @NotBlank
        @Size(max = 20)
        @EnumValidation(enumClass = TipoApiEnum.class, message = "O tipo de api deve ser SATVEG ou NASAPOWER")
        String tipoApiNome,

        @NotNull
        Long idTalhao
) {
    public ReqApi toEntity(TipoApi tipoApi) {
        return ReqApi.builder()
                .tipoParam(tipoParam)
                .dataAnalise(new Timestamp(System.currentTimeMillis()))
                .tipoApi(tipoApi)
                .build();
    }
}
