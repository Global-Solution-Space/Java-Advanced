package fiap.com.br.terranova.localizacao;

import fiap.com.br.terranova.localizacao.dto.LocalizacaoRequest;
import fiap.com.br.terranova.localizacao.dto.LocalizacaoResponse;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocalizacaoService {

    private final LocalizacaoRepository repository;

    public LocalizacaoService(LocalizacaoRepository repository) {
        this.repository = repository;
    }

    public LocalizacaoResponse criar(LocalizacaoRequest request) {
        Localizacao entity = complementarEntidade(new Localizacao(), request);
        return converterParaResponse(repository.save(entity));
    }

    public List<LocalizacaoResponse> listarTodos() {
        return repository.findAll().stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    public LocalizacaoResponse buscarPorId(Long id) {
        Localizacao entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Localização não encontrada com ID: " + id));
        return converterParaResponse(entity);
    }

    public LocalizacaoResponse atualizar(Long id, LocalizacaoRequest request) {
        Localizacao existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Localização não encontrada com ID: " + id));
        complementarEntidade(existente, request);
        return converterParaResponse(repository.save(existente));
    }

    public void deletar(Long id) {
        Localizacao entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Localização não encontrada com ID: " + id));
        repository.delete(entity);
    }

    private Localizacao complementarEntidade(Localizacao entity, LocalizacaoRequest request) {
        entity.setLoc_latitude(request.getLoc_latitude());
        entity.setLoc_longitude(request.getLoc_longitude());
        return entity;
    }

    private LocalizacaoResponse converterParaResponse(Localizacao entity) {
        LocalizacaoResponse response = new LocalizacaoResponse();
        response.setId_localizacao(entity.getId_localizacao());
        response.setLoc_latitude(entity.getLoc_latitude());
        response.setLoc_longitude(entity.getLoc_longitude());
        return response;
    }
}