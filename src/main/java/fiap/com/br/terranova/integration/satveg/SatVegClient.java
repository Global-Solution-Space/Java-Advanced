package fiap.com.br.terranova.integration.satveg;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "satVegClient", url = "https://api.cnptia.embrapa.br/satveg/v2")
public interface SatVegClient {

    @PostMapping("/series")
    SatVegDataResponse getSeries(@RequestBody SatVegDataRequest request);
}
