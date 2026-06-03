package fiap.com.br.terranova.config;

import fiap.com.br.terranova.reqapi.tipoapi.TipoApi;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApiEnum;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {
    private final TipoApiRepository tipoApiRepository;

    @Override
    public void run(String... args) {
        // Inicializar Tipos de API essenciais
        for (TipoApiEnum tipoEnum : TipoApiEnum.values()) {
            if (tipoApiRepository.findByTipoApi(tipoEnum.name()).isEmpty()) {
                tipoApiRepository.save(TipoApi.builder().tipoApi(tipoEnum.name()).build());
            }
        }
    }
}
