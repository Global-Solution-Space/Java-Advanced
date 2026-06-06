package fiap.com.br.terranova.produtor;

import fiap.com.br.terranova.exception.ResourceNotFoundException;
import fiap.com.br.terranova.produtor.dto.ProdutorRequest;
import fiap.com.br.terranova.produtor.dto.ProdutorResponse;
import fiap.com.br.terranova.propriedade.Propriedade;
import fiap.com.br.terranova.propriedade.PropriedadeRepository;
import fiap.com.br.terranova.propriedade.PropriedadeService;
import fiap.com.br.terranova.telefone.Telefone;
import fiap.com.br.terranova.telefone.TelefoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutorService {

    private final ProdutorRepository produtorRepository;
    private final TelefoneRepository telefoneRepository;
    private final PropriedadeRepository propriedadeRepository;
    private final PropriedadeService propriedadeService;

    public Page<ProdutorResponse> findAll(Pageable pageable) {
        return produtorRepository.findAll(pageable).map(ProdutorResponse::fromEntity);
    }

    public ProdutorResponse findById(Long id) {
        return ProdutorResponse.fromEntity(findProdutorById(id));
    }

    @Transactional
    public ProdutorResponse create(ProdutorRequest request) {
        Produtor produtor = produtorRepository.save(request.toEntity());

        if (request.telefone() != null) {
            Telefone telefone = request.telefone().toEntity(produtor);
            telefoneRepository.save(telefone);
        }

        return ProdutorResponse.fromEntity(produtor);
    }

    @Transactional
    public ProdutorResponse update(Long id, ProdutorRequest request) {
        findProdutorById(id);
        Produtor entity = request.toEntity();
        entity.setIdProdutor(id);
        return ProdutorResponse.fromEntity(produtorRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        Produtor entity = findProdutorById(id);
        List<Propriedade> propriedades = propriedadeRepository.findByProdutorIdProdutor(id);
        for (Propriedade propriedade : propriedades) {
            propriedadeService.delete(propriedade.getIdPropriedade());
        }
        telefoneRepository.deleteAll(telefoneRepository.findAllByProdutorIdProdutor(id));
        produtorRepository.delete(entity);
    }

    private Produtor findProdutorById(Long id) {
        return produtorRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Produtor com id " + id + " não encontrado."));
    }
}
