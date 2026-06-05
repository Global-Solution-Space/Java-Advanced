package fiap.com.br.terranova.propriedade;

import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.localizacao.Localizacao;
import fiap.com.br.terranova.localizacao.LocalizacaoRepository;
import fiap.com.br.terranova.produtor.Produtor;
import fiap.com.br.terranova.produtor.ProdutorRepository;
import fiap.com.br.terranova.propriedade.dto.PropriedadeRequest;
import fiap.com.br.terranova.propriedade.dto.PropriedadeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropriedadeService {

    private final PropriedadeRepository propriedadeRepository;
    private final ProdutorRepository produtorRepository;
    private final LocalizacaoRepository localizacaoRepository;

    public Page<PropriedadeResponse> findAll(Pageable pageable) {
        return propriedadeRepository.findAll(pageable).map(PropriedadeResponse::fromEntity);
    }

    public List<PropriedadeResponse> findByProdutor(Long idProdutor) {
        return propriedadeRepository.findByProdutorIdProdutor(idProdutor)
                .stream()
                .map(PropriedadeResponse::fromEntity)
                .toList();
    }

    public PropriedadeResponse findById(Long id) {
        return PropriedadeResponse.fromEntity(findPropriedadeById(id));
    }

    @Transactional
    public PropriedadeResponse create(PropriedadeRequest request) {
        return PropriedadeResponse.fromEntity(propriedadeRepository.save(request.toEntity(
                getProdutor(request.idProdutor()),
                getLocalizacao(request.idLocalizacao()))));
    }

    @Transactional
    public PropriedadeResponse update(Long id, PropriedadeRequest request) {
        findPropriedadeById(id);
        Propriedade entity = request.toEntity(
                getProdutor(request.idProdutor()),
                getLocalizacao(request.idLocalizacao()));
        entity.setIdPropriedade(id);
        return PropriedadeResponse.fromEntity(propriedadeRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        Propriedade entity = findPropriedadeById(id);
        propriedadeRepository.delete(entity);
    }

    private Propriedade findPropriedadeById(Long id) {
        return propriedadeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Propriedade com id " + id + " não encontrada."));
    }

    private Produtor getProdutor(Long id) {
        return produtorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Produtor com id " + id + " não encontrado."));
    }

    private Localizacao getLocalizacao(Long id) {
        return localizacaoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Localização com id " + id + " não encontrada."));
    }
}
