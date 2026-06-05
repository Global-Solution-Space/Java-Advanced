package fiap.com.br.terranova.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI terranovaOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Terra Nova API")
                        .description("API REST para monitoramento agrícola inteligente integrado com NASA e SatVeg Embrapa.")
                        .version("v1.0.0"))
                .tags(List.of(
                        new Tag().name("Produtor").description("Gerenciamento de produtores rurais"),
                        new Tag().name("Telefone").description("Gerenciamento de contatos telefônicos"),
                        new Tag().name("Localização").description("Gerenciamento de geolocalizações"),
                        new Tag().name("Propriedade").description("Gerenciamento de propriedades rurais"),
                        new Tag().name("Talhão").description("Gerenciamento de talhões e suas plantações"),
                        new Tag().name("Tipo de Plantação").description("Gerenciamento dos tipos de culturas"),
                        new Tag().name("Integração (Req API)").description("Orquestrador de dados de APIs externas (NASA POWER / SatVeg)"),
                        new Tag().name("Dado Temporal").description("Séries temporais unificadas de dados climáticos e vegetativos"),
                        new Tag().name("Alerta Agrícola").description("Alertas baseados na análise cruzada dos dados")
                ));
    }
}