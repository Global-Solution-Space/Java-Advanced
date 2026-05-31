package fiap.com.br.terranova.satveg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SatVegRepository extends JpaRepository<SatVeg, Long> {
}