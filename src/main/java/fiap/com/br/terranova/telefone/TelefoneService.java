package fiap.com.br.terranova.telefone;

import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.produtor.Produtor;
import fiap.com.br.terranova.produtor.ProdutorRepository;
import fiap.com.br.terranova.telefone.dto.TelefoneRequest;
import fiap.com.br.terranova.telefone.dto.TelefoneResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TelefoneService {

    private final TelefoneRepository telefoneRepository;
    private final ProdutorRepository produtorRepository;

    public Page<TelefoneResponse> findAll(Pageable pageable) {
        return telefoneRepository.findAll(pageable).map(TelefoneResponse::fromEntity);
    }

    public TelefoneResponse findById(Long id) {
        return TelefoneResponse.fromEntity(findTelefoneById(id));
    }

    @Transactional
    public TelefoneResponse create(TelefoneRequest request) {
        return TelefoneResponse.fromEntity(telefoneRepository.save(request.toEntity(getProdutor(request.idProdutor()))));
    }

    @Transactional
    public TelefoneResponse update(Long id, TelefoneRequest request) {
        findTelefoneById(id);
        Telefone entity = request.toEntity(getProdutor(request.idProdutor()));
        entity.setIdTelefone(id);
        return TelefoneResponse.fromEntity(telefoneRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        Telefone entity = findTelefoneById(id);
        telefoneRepository.delete(entity);
    }

    private Telefone findTelefoneById(Long id) {
        return telefoneRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Telefone com id " + id + " não encontrado."));
    }

    private Produtor getProdutor(Long id) {
        return produtorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Produtor com id " + id + " não encontrado."));
    }
}
