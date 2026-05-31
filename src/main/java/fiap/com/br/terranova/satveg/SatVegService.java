package fiap.com.br.terranova.satveg;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SatVegService {

    private final SatVegRepository repository;
    private final TalhaoRepository talhaoRepository;
    private final SatVegClient satVegClient;

    public Page<SatVegResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(SatVegResponse::fromEntity);
    }

    public SatVegResponse findById(Long id) {
        return SatVegResponse.fromEntity(findSatVegById(id));
    }

    @Transactional
    public SatVegResponse create(SatVegRequest request) {
        Talhao talhao = getTalhao(request.idTalhao());

        try {
            log.info("Buscando series temporais na Embrapa SATveg para o Talhao {}", talhao.getIdTalhao());
            SatVegDataResponse apiResponse = satVegClient.getSeries(buildApiRequest(request, talhao));

            log.info("Sucesso! O SATveg retornou {} pontos de serie temporal para a localizacao informada.", apiResponse.listaSerie().size());

        } catch (Exception e) {
            log.error("Erro ao integrar com a API da Embrapa SATveg", e);
        }

        return SatVegResponse.fromEntity(repository.save(request.toEntity(talhao)));
    }

    @Transactional
    public SatVegResponse update(Long id, SatVegRequest request) {
        SatVeg existingEntity = findSatVegById(id);
        SatVeg entity = request.toEntity(getTalhao(request.idTalhao()));
        entity.setIdSatveg(id);
        entity.setDataAnalise(existingEntity.getDataAnalise());
        return SatVegResponse.fromEntity(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        SatVeg entity = findSatVegById(id);
        repository.delete(entity);
    }

    private SatVeg findSatVegById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SatVeg com id " + id + " nao encontrado."));
    }

    private Talhao getTalhao(Long id) {
        return talhaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Talhao com id " + id + " nao encontrado."));
    }

    private SatVegDataRequest buildApiRequest(SatVegRequest request, Talhao talhao) {
        return SatVegDataRequest.builder()
                .tipoPerfil(request.tipoPerfil().intValue() == 1 ? "ndvi" : "evi")
                .satelite(request.satelite().intValue() == 1 ? "comb" : "modis")
                .preFiltro(request.preFiltro())
                .filtro(request.filtro())
                .parametroFiltro(request.parametroFiltro())
                .latitude(talhao.getLocalizacao().getLocLatitude())
                .longitude(talhao.getLocalizacao().getLocLongitude())
                .build();
    }
}