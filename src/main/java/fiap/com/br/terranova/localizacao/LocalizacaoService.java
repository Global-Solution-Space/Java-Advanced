package fiap.com.br.terranova.localizacao;

import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.localizacao.dto.LocalizacaoRequest;
import fiap.com.br.terranova.localizacao.dto.LocalizacaoResponse;
import fiap.com.br.terranova.propriedade.PropriedadeRepository;
import fiap.com.br.terranova.talhao.TalhaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LocalizacaoService {

    private final LocalizacaoRepository repository;
    private final PropriedadeRepository propriedadeRepository;
    private final TalhaoRepository talhaoRepository;

    public Page<LocalizacaoResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(LocalizacaoResponse::fromEntity);
    }

    public LocalizacaoResponse findById(Long id) {
        return LocalizacaoResponse.fromEntity(findLocalizacaoById(id));
    }

    @Transactional
    public LocalizacaoResponse create(LocalizacaoRequest request) {
        // Verifica se a localização já existe no banco de dados para evitar duplicidade
        return repository.findByLocLatitudeAndLocLongitude(request.locLatitude(), request.locLongitude())
                .map(LocalizacaoResponse::fromEntity)
                .orElseGet(() -> LocalizacaoResponse.fromEntity(repository.save(request.toEntity())));
    }

    @Transactional
    public LocalizacaoResponse update(Long id, LocalizacaoRequest request) {
        findLocalizacaoById(id);
        Localizacao entity = request.toEntity();
        entity.setIdLocalizacao(id);
        return LocalizacaoResponse.fromEntity(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        Localizacao entity = findLocalizacaoById(id);
        if (propriedadeRepository.existsByLocalizacaoIdLocalizacao(id) || talhaoRepository.existsByLocalizacaoIdLocalizacao(id)) throw new IllegalArgumentException("Localização em uso por propriedade ou talhão. Remova ou atualize os vínculos antes de excluir.");
        repository.delete(entity);
    }

    private Localizacao findLocalizacaoById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Localização com id " + id + " não encontrada."));
    }
}