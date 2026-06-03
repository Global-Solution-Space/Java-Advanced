package fiap.com.br.terranova.talhao;

import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.localizacao.Localizacao;
import fiap.com.br.terranova.localizacao.LocalizacaoRepository;
import fiap.com.br.terranova.propriedade.Propriedade;
import fiap.com.br.terranova.propriedade.PropriedadeRepository;
import fiap.com.br.terranova.talhao.dto.TalhaoRequest;
import fiap.com.br.terranova.talhao.dto.TalhaoResponse;
import fiap.com.br.terranova.tipoplantacao.TipoPlantacao;
import fiap.com.br.terranova.tipoplantacao.TipoPlantacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TalhaoService {

    private final TalhaoRepository talhaoRepository;
    private final TipoPlantacaoRepository tipoPlantacaoRepository;
    private final PropriedadeRepository propriedadeRepository;
    private final LocalizacaoRepository localizacaoRepository;

    public Page<TalhaoResponse> findAll(Pageable pageable) {
        return talhaoRepository.findAll(pageable).map(TalhaoResponse::fromEntity);
    }

    public List<TalhaoResponse> findByProdutor(Long idProdutor) {
        return talhaoRepository.findByPropriedadeProdutorIdProdutor(idProdutor)
                .stream()
                .map(TalhaoResponse::fromEntity)
                .toList();
    }

    public TalhaoResponse findById(Long id) {
        return TalhaoResponse.fromEntity(findTalhaoById(id));
    }

    @Transactional
    public TalhaoResponse create(TalhaoRequest request) {
        return TalhaoResponse.fromEntity(talhaoRepository.save(request.toEntity(
                getTipoPlantacao(request.idTipoPlantacao()),
                getPropriedade(request.idPropriedade()),
                getLocalizacao(request.idLocalizacao())
        )));
    }

    @Transactional
    public TalhaoResponse update(Long id, TalhaoRequest request) {
        findTalhaoById(id);
        Talhao entity = request.toEntity(
                getTipoPlantacao(request.idTipoPlantacao()),
                getPropriedade(request.idPropriedade()),
                getLocalizacao(request.idLocalizacao())
        );
        entity.setIdTalhao(id);
        return TalhaoResponse.fromEntity(talhaoRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        Talhao entity = findTalhaoById(id);
        talhaoRepository.delete(entity);
    }

    private Talhao findTalhaoById(Long id) {
        return talhaoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Talhao com id " + id + " nao encontrado."));
    }

    private TipoPlantacao getTipoPlantacao(Long id) {
        return tipoPlantacaoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("TipoPlantacao com id " + id + " nao encontrado."));
    }

    private Propriedade getPropriedade(Long id) {
        return propriedadeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Propriedade com id " + id + " nao encontrada."));
    }

    private Localizacao getLocalizacao(Long id) {
        return localizacaoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Localizacao com id " + id + " nao encontrada."));
    }
}
