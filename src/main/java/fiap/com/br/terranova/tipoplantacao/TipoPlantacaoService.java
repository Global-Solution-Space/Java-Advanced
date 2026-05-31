package fiap.com.br.terranova.tipoplantacao;

import fiap.com.br.terranova.tipoplantacao.dto.TipoPlantacaoRequest;
import fiap.com.br.terranova.tipoplantacao.dto.TipoPlantacaoResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoPlantacaoService {

    private final TipoPlantacaoRepository repository;

    public TipoPlantacaoService(TipoPlantacaoRepository repository) {
        this.repository = repository;
    }

    public TipoPlantacaoResponse criar(TipoPlantacaoRequest request) {
        TipoPlantacao entity = new TipoPlantacao();
        entity.setTipo_plant(request.getTipo_plant());
        return converterParaResponse(repository.save(entity));
    }

    public List<TipoPlantacaoResponse> listarTodos() {
        return repository.findAll().stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    public TipoPlantacaoResponse buscarPorId(Long id) {
        TipoPlantacao entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de Plantação não encontrado com ID: " + id));
        return converterParaResponse(entity);
    }

    public TipoPlantacaoResponse atualizar(Long id, TipoPlantacaoRequest request) {
        TipoPlantacao existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de Plantação não encontrado com ID: " + id));
        existente.setTipo_plant(request.getTipo_plant());
        return converterParaResponse(repository.save(existente));
    }

    public void deletar(Long id) {
        TipoPlantacao entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de Plantação não encontrado com ID: " + id));
        repository.delete(entity);
    }

    private TipoPlantacaoResponse converterParaResponse(TipoPlantacao entity) {
        TipoPlantacaoResponse response = new TipoPlantacaoResponse();
        response.setId_tipo_plant(entity.getId_tipo_plant());
        response.setTipo_plant(entity.getTipo_plant());
        return response;
    }
}