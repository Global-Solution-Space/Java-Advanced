package fiap.com.br.terranova.alerta;

import fiap.com.br.terranova.alerta.dto.AlertaRequest;
import fiap.com.br.terranova.alerta.dto.AlertaResponse;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.nasapower.NasaPower;
import fiap.com.br.terranova.nasapower.NasaPowerRepository;
import fiap.com.br.terranova.satveg.SatVeg;
import fiap.com.br.terranova.satveg.SatVegRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final NasaPowerRepository nasaPowerRepository;
    private final SatVegRepository satVegRepository;

    public Page<AlertaResponse> findAll(Pageable pageable) {
        return alertaRepository.findAll(pageable).map(AlertaResponse::fromEntity);
    }

    public AlertaResponse findById(Long id) {
        return AlertaResponse.fromEntity(findAlertaById(id));
    }

    @Transactional
    public AlertaResponse create(AlertaRequest request) {
        return AlertaResponse.fromEntity(alertaRepository.save(request.toEntity(getSatVeg(request.idSatveg()), getNasaPower(request.idNasapower()))));
    }

    @Transactional
    public AlertaResponse update(Long id, AlertaRequest request) {
        Alerta existingEntity = findAlertaById(id);
        Alerta entity = request.toEntity(getSatVeg(request.idSatveg()), getNasaPower(request.idNasapower()));
        entity.setIdAlerta(id);
        entity.setDataAlerta(existingEntity.getDataAlerta());
        return AlertaResponse.fromEntity(alertaRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        Alerta entity = findAlertaById(id);
        alertaRepository.delete(entity);
    }

    private Alerta findAlertaById(Long id) {
        return alertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta com id " + id + " nao encontrado."));
    }

    private SatVeg getSatVeg(Long id) {
        return satVegRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SatVeg com id " + id + " nao encontrado."));
    }

    private NasaPower getNasaPower(Long id) {
        return nasaPowerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NasaPower com id " + id + " nao encontrado."));
    }
}