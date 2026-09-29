package eCommerce.GamesRun.repository;

import eCommerce.GamesRun.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
