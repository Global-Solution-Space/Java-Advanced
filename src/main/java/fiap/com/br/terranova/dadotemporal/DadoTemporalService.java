package fiap.com.br.terranova.dadotemporal;

import fiap.com.br.terranova.dadotemporal.dto.DadoTemporalResponse;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DadoTemporalService {

    private final DadoTemporalRepository repository;

    public List<DadoTemporalResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(DadoTemporalResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public DadoTemporalResponse findById(Long id) {
        return repository.findById(id)
                .map(DadoTemporalResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Dado temporal com id " + id + " não encontrado"));
    }

    public Page<DadoTemporalResponse> findByTalhao(Long idTalhao, Pageable pageable) {
        return repository.findByTalhaoIdTalhao(idTalhao, pageable)
                .map(DadoTemporalResponse::fromEntity);
    }

    public Page<DadoTemporalResponse> findByReqApi(Long idReqApi, Pageable pageable) {
        return repository.findByReqApiIdApi(idReqApi, pageable)
                .map(DadoTemporalResponse::fromEntity);
    }
}
