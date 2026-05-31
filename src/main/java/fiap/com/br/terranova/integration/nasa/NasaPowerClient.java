package fiap.com.br.terranova.integration.nasa;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@FeignClient(name = "nasaPowerClient", url = "https://power.larc.nasa.gov/api")
public interface NasaPowerClient {

    @GetMapping("/temporal/daily/point")
    NasaPowerDataResponse getDailyData(@SpringQueryMap Map<String, Object> query);
}
