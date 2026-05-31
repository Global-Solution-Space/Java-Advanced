package fiap.com.br.terranova;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TerranovaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TerranovaApplication.class, args);
    }

}
