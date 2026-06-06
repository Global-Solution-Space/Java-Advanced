package fiap.com.br.terranova;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@EnableFeignClients
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@RestController
public class TerranovaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TerranovaApplication.class, args);
    }

    @GetMapping({"/api", "/api/"})
    public Map<String, Object> health() {
        return Map.of("status", "UP", "application", "Terra Nova API", "message", "API em funcionamento", "docs", "/swagger-ui.html");
    }
}
