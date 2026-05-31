package fiap.com.br.terranova.nasapower;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NasaPowerRepository extends JpaRepository<NasaPower, Long> {
}