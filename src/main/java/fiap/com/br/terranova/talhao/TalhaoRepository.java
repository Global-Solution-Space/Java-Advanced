package fiap.com.br.terranova.talhao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TalhaoRepository extends JpaRepository<Talhao, Long> {
}