package fiap.com.br.terranova.alerta;

import fiap.com.br.terranova.alerta.dto.AlertaRequest;
import fiap.com.br.terranova.alerta.dto.AlertaResponse;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.talhao.TalhaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final TalhaoRepository talhaoRepository;

    public Page<AlertaResponse> findAll(Pageable pageable) {
        return alertaRepository.findAll(pageable).map(AlertaResponse::fromEntity);
    }

    public List<AlertaResponse> findByProdutor(Long idProdutor) {
        return alertaRepository.findByTalhaoPropriedadeProdutorIdProdutor(idProdutor)
                .stream()
                .map(AlertaResponse::fromEntity)
                .toList();
    }

    public AlertaResponse findById(Long id) {
        return AlertaResponse.fromEntity(findAlertaById(id));
    }

    @Transactional
    public AlertaResponse create(AlertaRequest request) {
        return AlertaResponse.fromEntity(alertaRepository.save(request.toEntity(getTalhao(request.idTalhao()))));
    }

    @Transactional
    public AlertaResponse update(Long id, AlertaRequest request) {
        Alerta existingEntity = findAlertaById(id);
        Alerta entity = request.toEntity(getTalhao(request.idTalhao()));
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

    private Talhao getTalhao(Long id) {
        return talhaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Talhao com id " + id + " nao encontrado."));
    }
}