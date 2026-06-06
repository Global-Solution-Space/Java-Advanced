package fiap.com.br.terranova.alerta;

import fiap.com.br.terranova.alerta.dto.AlertaRequest;
import fiap.com.br.terranova.alerta.dto.AlertaResponse;
import fiap.com.br.terranova.dadotemporal.DadoTemporal;
import fiap.com.br.terranova.dadotemporal.DadoTemporalRepository;
import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.talhao.Talhao;
import fiap.com.br.terranova.talhao.TalhaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final TalhaoRepository talhaoRepository;
    private final DadoTemporalRepository dadoTemporalRepository;

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
    public AlertaResponse resolver(Long id) {
        Alerta entity = findAlertaById(id);
        entity.setResolvido("S");
        return AlertaResponse.fromEntity(alertaRepository.save(entity));
    }

    @Transactional
    public AlertaResponse reabrir(Long id) {
        Alerta entity = findAlertaById(id);
        entity.setResolvido("N");
        return AlertaResponse.fromEntity(alertaRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        Alerta entity = findAlertaById(id);
        alertaRepository.delete(entity);
    }

    // ANÁLISE HISTÓRICA — chamado após cada fetch externo
    @Transactional
    public void analisarEGerarAlertas(Talhao talhao, String tipoApiNome) {
        String tipo = tipoApiNome.toLowerCase();

        LocalDate janela = switch (tipo) {
            case "nasapower" -> LocalDate.now().minusDays(15);
            case "satveg" -> LocalDate.now().minusDays(90);
            default -> {
                log.warn("Tipo de API desconhecido para análise: {}", tipoApiNome);
                yield LocalDate.now().minusDays(15);
            }
        };

        List<DadoTemporal> dados = dadoTemporalRepository
                .buscarDadosParaAnalise(talhao.getIdTalhao(), tipoApiNome, janela);

        if (dados == null || dados.isEmpty()) {
            log.info("Nenhum dado histórico encontrado para análise. Talhão={}, Tipo={}", talhao.getIdTalhao(), tipoApiNome);
            return;
        }

        if ("nasapower".equals(tipo)) {
            analisarNasa(talhao, dados);
        } else if ("satveg".equals(tipo)) {
            analisarSatVeg(talhao, dados);
        }
    }

    // ANÁLISE NASA POWER — janela de 15 dias, acumula chuva
    private void analisarNasa(Talhao talhao, List<DadoTemporal> dadosOrdenados) {
        double chuvaAcumulada15dias = dadosOrdenados.stream().mapToDouble(DadoTemporal::getValor).sum();
        double chuvaAcumulada3dias = dadosOrdenados.stream().limit(3).mapToDouble(DadoTemporal::getValor).sum();

        log.info("NASA análise: talhao={}, chuva15d={}mm, chuva3d={}mm", talhao.getIdTalhao(), String.format("%.1f", chuvaAcumulada15dias), String.format("%.1f", chuvaAcumulada3dias));

        if (chuvaAcumulada3dias > 80.0) {
            criarAlerta(talhao,
                    "Risco de Alagamento (NASA)",
                    "Chuva extrema detectada nos últimos 3 dias (" + String.format("%.1f", chuvaAcumulada3dias) + " mm). Risco de erosão e asfixia radicular.",
                    NivelAlerta.ALTO);
        } else if (chuvaAcumulada15dias < 10.0) {
            criarAlerta(talhao,
                    "Seca Severa (NASA)",
                    "Apenas " + String.format("%.1f", chuvaAcumulada15dias) + " mm de chuva acumulada nos últimos 15 dias.",
                    NivelAlerta.CRITICO);
        } else if (chuvaAcumulada15dias < 25.0) {
            criarAlerta(talhao,
                    "Estresse Hídrico (NASA)",
                    "Baixa precipitação acumulada nos últimos 15 dias (" + String.format("%.1f", chuvaAcumulada15dias) + " mm).",
                    NivelAlerta.MEDIO);
        }
    }

    // ANÁLISE SATVEG — janela de 90 dias, avalia o NDVI mais recente
    private void analisarSatVeg(Talhao talhao, List<DadoTemporal> dadosOrdenados) {
        DadoTemporal ultimoDado = dadosOrdenados.get(0);
        double ndvi = ultimoDado.getValor();

        log.info("SATveg análise: talhao={}, NDVI={}", talhao.getIdTalhao(), String.format("%.2f", ndvi));

        if (ndvi < 0.2) {
            criarAlerta(talhao,
                    "Anomalia Vegetativa Severa (SATVEG)",
                    "O NDVI atual caiu para " + String.format("%.2f", ndvi) + ". Possível falha na cultura ou solo exposto.",
                    NivelAlerta.CRITICO);
        } else if (ndvi < 0.4) {
            criarAlerta(talhao,
                    "Baixo Vigor Vegetativo (SATVEG)",
                    "O NDVI atual é de " + String.format("%.2f", ndvi) + ". Monitore para pragas, doenças ou estresse nutricional.",
                    NivelAlerta.MEDIO);
        }
    }

    private void criarAlerta(Talhao talhao, String titulo, String descricao, NivelAlerta nivel) {
        if (alertaRepository.existsByTalhaoAndTituloAndResolvido(talhao, titulo, "N")) {
            log.info("Alerta automático '{}' para o Talhão {} ignorado (já existe um ativo).", titulo, talhao.getIdTalhao());
            return;
        }
        Alerta alerta = Alerta.builder()
                .titulo(titulo)
                .descricao(descricao)
                .nivelAlerta(nivel.name())
                .resolvido("N")
                .dataAlerta(new Timestamp(System.currentTimeMillis()))
                .talhao(talhao)
                .build();
        alertaRepository.save(alerta);
        log.info("Novo alerta gerado automaticamente: {} para o Talhão {}", titulo, talhao.getIdTalhao());
    }

    private Alerta findAlertaById(Long id) {
        return alertaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Alerta com id " + id + " não encontrado."));
    }

    private Talhao getTalhao(Long id) {
        return talhaoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Talhao com id " + id + " não encontrado."));
    }
}
