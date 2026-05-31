package fiap.com.br.terranova.satveg;

import fiap.com.br.terranova.satveg.dto.SatVegRequest;
import fiap.com.br.terranova.satveg.dto.SatVegResponse;
import fiap.com.br.terranova.talhao.TalhaoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SatVegService {

    private final SatVegRepository repository;
    private final TalhaoRepository talhaoRepository;

    public SatVegService(SatVegRepository repository, TalhaoRepository talhaoRepository) {
        this.repository = repository;
        this.talhaoRepository = talhaoRepository;
    }

    public SatVegResponse criar(SatVegRequest request) {
        SatVeg entity = complementarEntidade(new SatVeg(), request);
        return converterParaResponse(repository.save(entity));
    }

    public List<SatVegResponse> listarTodos() {
        return repository.findAll().stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    public SatVegResponse buscarPorId(Long id) {
        SatVeg entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SatVeg não encontrado com o ID: " + id));
        return converterParaResponse(entity);
    }

    public SatVegResponse atualizar(Long id, SatVegRequest request) {
        SatVeg existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SatVeg não encontrado com o ID: " + id));

        complementarEntidade(existente, request);
        return converterParaResponse(repository.save(existente));
    }

    public void deletar(Long id) {
        SatVeg entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("SatVeg não encontrado com o ID: " + id));
        repository.delete(entity);
    }

    private SatVeg complementarEntidade(SatVeg entity, SatVegRequest request) {
        entity.setTipo_perfil(request.getTipo_perfil());
        entity.setSatelite(request.getSatelite());
        entity.setPre_filtro(request.getPre_filtro());
        entity.setFiltro(request.getFiltro());
        entity.setParametro_filtro(request.getParametro_filtro());
        entity.setPoligono(request.getPoligono());
        entity.setTodas_estatisticas(request.getTodas_estatisticas());
        entity.setData_analise(request.getData_analise());

        if (request.getId_talhao() != null) {
            entity.setTalhao(talhaoRepository.findById(request.getId_talhao())
                    .orElseThrow(() -> new RuntimeException("Talhão não encontrado com ID: " + request.getId_talhao())));
        }
        return entity;
    }

    private SatVegResponse converterParaResponse(SatVeg entity) {
        SatVegResponse response = new SatVegResponse();
        response.setId_satveg(entity.getId_satveg());
        response.setTipo_perfil(entity.getTipo_perfil());
        response.setSatelite(entity.getSatelite());
        response.setPre_filtro(entity.getPre_filtro());
        response.setFiltro(entity.getFiltro());
        response.setParametro_filtro(entity.getParametro_filtro());
        response.setPoligono(entity.getPoligono());
        response.setTodas_estatisticas(entity.getTodas_estatisticas());
        response.setData_analise(entity.getData_analise());

        if (entity.getTalhao() != null) {
            response.setId_talhao(entity.getTalhao().getId_talhao());
        }
        return response;
    }
}