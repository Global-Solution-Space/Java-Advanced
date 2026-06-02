package fiap.com.br.terranova.satveg;

import fiap.com.br.terranova.dadotemporal.DadoTemporal;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.integration.satveg.SatVegClient;
import fiap.com.br.terranova.integration.satveg.SatVegDataRequest;
import fiap.com.br.terranova.integration.satveg.SatVegDataResponse;
import fiap.com.br.terranova.satveg.dto.SatVegRequest;
import fiap.com.br.terranova.satveg.dto.SatVegResponse;
import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.talhao.TalhaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SatVegService {

    private final SatVegRepository repository;
    private final TalhaoRepository talhaoRepository;
    private final SatVegClient satVegClient;

    @Value("${satveg.api.token:Bearer e97dab05-eedc-39b9-a3fd-fa83cb5fef5e}")
    private String satVegToken;

    public Page<SatVegResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(SatVegResponse::fromEntity);
    }

    public SatVegResponse findById(Long id) {
        return SatVegResponse.fromEntity(findSatVegById(id));
    }

    @Transactional
    public SatVegResponse create(SatVegRequest request) {
        Talhao talhao = getTalhao(request.idTalhao());
        SatVeg entity = request.toEntity(talhao);

        fetchAndSetSatVegData(entity);

        return SatVegResponse.fromEntity(repository.save(entity));
    }

    @Transactional
    public SatVegResponse update(Long id, SatVegRequest request) {
        SatVeg existingEntity = findSatVegById(id);
        SatVeg entity = request.toEntity(getTalhao(request.idTalhao()));
        entity.setIdSatveg(id);
        entity.setDataAnalise(existingEntity.getDataAnalise());

        // Limpa os dados antigos e busca novos
        entity.setDados(new ArrayList<>());
        fetchAndSetSatVegData(entity);

        return SatVegResponse.fromEntity(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        SatVeg entity = findSatVegById(id);
        repository.delete(entity);
    }

    private SatVeg findSatVegById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("SatVeg com id " + id + " nao encontrado."));
    }

    private Talhao getTalhao(Long id) {
        return talhaoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Talhao com id " + id + " nao encontrado."));
    }

    private void fetchAndSetSatVegData(SatVeg entity) {
        try {
            log.info("Buscando series temporais na Embrapa SATveg para o Talhao {}", entity.getTalhao().getIdTalhao());
            SatVegDataResponse apiResponse = satVegClient.getSeries(satVegToken, buildApiRequest(entity));

            List<DadoTemporal> dados = new ArrayList<>();
            for (int i = 0; i < apiResponse.listaSerie().size(); i++) {
                String dataString = apiResponse.listaDatas().get(i);
                // Armazena apenas a série temporal a partir de 2020
                if (dataString != null && dataString.compareTo("2020-01-01") >= 0) {
                    dados.add(DadoTemporal.criarParaSatVeg(dataString, apiResponse.listaSerie().get(i), entity));
                }
            }

            entity.setDados(dados);
            log.info("Sucesso! O SATveg retornou {} pontos de serie temporal filtrados.", dados.size());

        } catch (Exception e) {
            log.error("Erro ao integrar com a API da Embrapa SATveg", e);
        }
    }

    private SatVegDataRequest buildApiRequest(SatVeg entity) {
        return SatVegDataRequest.builder()
                .tipoPerfil(entity.getTipoPerfil())
                .satelite("comb")
                .preFiltro(3)
                .filtro("sav")
                .parametroFiltro(4)
                .latitude(entity.getTalhao().getLocalizacao().getLocLatitude())
                .longitude(entity.getTalhao().getLocalizacao().getLocLongitude())
                .build();
    }
}