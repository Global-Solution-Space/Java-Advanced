package fiap.com.br.terranova.config;

import fiap.com.br.terranova.reqapi.tipoapi.TipoApi;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApiEnum;
import fiap.com.br.terranova.reqapi.tipoapi.TipoApiRepository;
import fiap.com.br.terranova.tipoplantacao.TipoPlantacao;
import fiap.com.br.terranova.tipoplantacao.TipoPlantacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {
    private final TipoApiRepository tipoApiRepository;
    private final TipoPlantacaoRepository tipoPlantacaoRepository;

    @Override
    public void run(String... args) {
        // Inicializar Tipos de API essenciais
        for (TipoApiEnum tipoEnum : TipoApiEnum.values()) {
            if (tipoApiRepository.findByTipoApi(tipoEnum.name()).isEmpty()) {
                tipoApiRepository.save(TipoApi.builder().tipoApi(tipoEnum.name()).build());
            }
        }

        // Inicializar Tipos de Plantação
        List<String> plantacoes = List.of("Soja", "Milho", "Algodão", "Café", "Cana-de-Açúcar", "Trigo", "Arroz", "Feijão", "Laranja", "Uva");
        for (String plant : plantacoes) {
            if (!tipoPlantacaoRepository.existsByTipoPlant(plant)) {
                tipoPlantacaoRepository.save(TipoPlantacao.builder().tipoPlant(plant).build());
            }
        }
    }
}
