package fiap.com.br.terranova.dadotemporal;

import fiap.com.br.terranova.dadotemporal.dto.DadoTemporalResponse;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DadoTemporalService {

    private final DadoTemporalRepository repository;

    public DadoTemporalResponse findById(Long id) {
        return repository.findById(id)
                .map(DadoTemporalResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Dado temporal com id " + id + " nao encontrado"));
    }

    public List<DadoTemporalResponse> findByTalhao(Long idTalhao) {
        return repository.findByTalhaoIdTalhao(idTalhao)
                .stream()
                .map(DadoTemporalResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<DadoTemporalResponse> findByReqApi(Long idReqApi) {
        return repository.findByReqApiIdApi(idReqApi)
                .stream()
                .map(DadoTemporalResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
