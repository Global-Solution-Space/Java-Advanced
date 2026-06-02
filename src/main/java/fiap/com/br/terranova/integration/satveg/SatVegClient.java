package fiap.com.br.terranova.integration.satveg;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "satVegClient", url = "https://api.cnptia.embrapa.br/satveg/v2")
public interface SatVegClient {

    @PostMapping("/series")
    SatVegDataResponse getSeries(@RequestHeader("Authorization") String token, @RequestBody SatVegDataRequest request);
}
