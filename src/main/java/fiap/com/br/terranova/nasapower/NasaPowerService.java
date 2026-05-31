package fiap.com.br.terranova.nasapower;

import fiap.com.br.terranova.nasapower.dto.NasaPowerRequest;
import fiap.com.br.terranova.nasapower.dto.NasaPowerResponse;
import fiap.com.br.terranova.talhao.TalhaoRepository; // <-- Importado
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NasaPowerService {

    private final NasaPowerRepository repository;
    private final TalhaoRepository talhaoRepository;

    public NasaPowerService(NasaPowerRepository repository, TalhaoRepository talhaoRepository) {
        this.repository = repository;
        this.talhaoRepository = talhaoRepository;
    }

    public NasaPowerResponse criar(NasaPowerRequest request) {
        NasaPower entity = complementarEntidade(new NasaPower(), request);
        return converterParaResponse(repository.save(entity));
    }

    public List<NasaPowerResponse> listarTodos() {
        return repository.findAll().stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    public NasaPowerResponse buscarPorId(Long id) {
        NasaPower entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("NasaPower não encontrado com o ID: " + id));
        return converterParaResponse(entity);
    }

    public NasaPowerResponse atualizar(Long id, NasaPowerRequest request) {
        NasaPower existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("NasaPower não encontrado com o ID: " + id));
        complementarEntidade(existente, request);
        return converterParaResponse(repository.save(existente));
    }

    public void deletar(Long id) {
        NasaPower entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("NasaPower não encontrado com o ID: " + id));
        repository.delete(entity);
    }

    private NasaPower complementarEntidade(NasaPower entity, NasaPowerRequest request) {
        entity.setData_inicio(request.getData_inicio());
        entity.setData_fim(request.getData_fim());
        entity.setLatitude(request.getLatitude());
        entity.setLongitude(request.getLongitude());
        entity.setElevacao(request.getElevacao());

        // Busca o Talhão e associa à entidade NasaPower
        if (request.getId_talhao() != null) {
            entity.setTalhao(talhaoRepository.findById(request.getId_talhao())
                    .orElseThrow(() -> new RuntimeException("Talhão não encontrado com ID: " + request.getId_talhao())));
        } else {
            entity.setTalhao(null);
        }

        return entity;
    }

    private NasaPowerResponse converterParaResponse(NasaPower entity) {
        NasaPowerResponse response = new NasaPowerResponse();
        response.setId_nasapower(entity.getId_nasapower());
        response.setData_inicio(entity.getData_inicio());
        response.setData_fim(entity.getData_fim());
        response.setLatitude(entity.getLatitude());
        response.setLongitude(entity.getLongitude());
        response.setElevacao(entity.getElevacao());

        // Mapeia o ID do talhão de volta para o Response DTO
        if (entity.getTalhao() != null) {
            response.setId_talhao(entity.getTalhao().getId_talhao());
        }

        return response;
    }
}