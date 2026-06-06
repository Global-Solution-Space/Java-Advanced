package fiap.com.br.terranova.produtor;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutorRepository extends JpaRepository<Produtor, Long> {
    boolean existsByEmail(String email);
}
