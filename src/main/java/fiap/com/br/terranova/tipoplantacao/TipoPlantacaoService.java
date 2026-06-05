package fiap.com.br.terranova.tipoplantacao;

import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.tipoplantacao.dto.TipoPlantacaoRequest;
import fiap.com.br.terranova.tipoplantacao.dto.TipoPlantacaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TipoPlantacaoService {

    private final TipoPlantacaoRepository repository;

    public Page<TipoPlantacaoResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(TipoPlantacaoResponse::fromEntity);
    }

    public TipoPlantacaoResponse findById(Long id) {
        return TipoPlantacaoResponse.fromEntity(findTipoPlantacaoById(id));
    }

    @Transactional
    public TipoPlantacaoResponse create(TipoPlantacaoRequest request) {
        return TipoPlantacaoResponse.fromEntity(repository.save(request.toEntity()));
    }

    @Transactional
    public TipoPlantacaoResponse update(Long id, TipoPlantacaoRequest request) {
        findTipoPlantacaoById(id);
        TipoPlantacao entity = request.toEntity();
        entity.setIdTipoPlant(id);
        return TipoPlantacaoResponse.fromEntity(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        TipoPlantacao entity = findTipoPlantacaoById(id);
        repository.delete(entity);
    }

    private TipoPlantacao findTipoPlantacaoById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TipoPlantacao com id " + id + " não encontrado."));
    }
}