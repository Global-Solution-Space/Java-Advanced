package fiap.com.br.terranova.alerta;

import fiap.com.br.terranova.alerta.dto.AlertaRequest;
import fiap.com.br.terranova.alerta.dto.AlertaResponse;
import fiap.com.br.terranova.nasapower.NasaPowerRepository;
import fiap.com.br.terranova.satveg.SatVegRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final NasaPowerRepository nasaPowerRepository;
    private final SatVegRepository satVegRepository;

    public AlertaService(AlertaRepository alertaRepository,
                         NasaPowerRepository nasaPowerRepository,
                         SatVegRepository satVegRepository) {
        this.alertaRepository = alertaRepository;
        this.nasaPowerRepository = nasaPowerRepository;
        this.satVegRepository = satVegRepository;
    }

    public AlertaResponse criar(AlertaRequest request) {
        Alerta alerta = complementarEntidade(new Alerta(), request);
        Alerta salvo = alertaRepository.save(alerta);
        return converterParaResponse(salvo);
    }

    public List<AlertaResponse> listarTodos() {
        return alertaRepository.findAll().stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    public AlertaResponse buscarPorId(Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta não encontrado com o ID: " + id));
        return converterParaResponse(alerta);
    }

    public AlertaResponse atualizar(Long id, AlertaRequest request) {
        Alerta alertaExistente = alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta não encontrado com o ID: " + id));

        Alerta alertaAtualizado = complementarEntidade(alertaExistente, request);
        Alerta salvo = alertaRepository.save(alertaAtualizado);
        return converterParaResponse(salvo);
    }

    public void deletar(Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta não encontrado com o ID: " + id));
        alertaRepository.delete(alerta);
    }

    private Alerta complementarEntidade(Alerta alerta, AlertaRequest request) {
        alerta.setTitulo(request.getTitulo());
        alerta.setDescricao(request.getDescricao());
        alerta.setNivel_alerta(request.getNivel_alerta());
        alerta.setResolvido(request.getResolvido());
        alerta.setData_alerta(request.getData_alerta());

        if (request.getId_nasapower() != null) {
            alerta.setNasaPower(nasaPowerRepository.findById(request.getId_nasapower())
                    .orElseThrow(() -> new RuntimeException("NasaPower não encontrado")));
        }

        if (request.getId_satveg() != null) {
            alerta.setSatVeg(satVegRepository.findById(request.getId_satveg())
                    .orElseThrow(() -> new RuntimeException("SatVeg não encontrado")));
        }

        return alerta;
    }

    private AlertaResponse converterParaResponse(Alerta alerta) {
        AlertaResponse response = new AlertaResponse();
        response.setId_alerta(alerta.getId_alerta());
        response.setTitulo(alerta.getTitulo());
        response.setDescricao(alerta.getDescricao());
        response.setNivel_alerta(alerta.getNivel_alerta());
        response.setResolvido(alerta.getResolvido());
        response.setData_alerta(alerta.getData_alerta());

        if (alerta.getNasaPower() != null) {
            response.setId_nasapower(alerta.getNasaPower().getId_nasapower());
        }
        if (alerta.getSatVeg() != null) {
            response.setId_satveg(alerta.getSatVeg().getId_satveg());
        }

        return response;
    }
}