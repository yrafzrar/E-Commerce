package eCommerce.GamesRun.repository;

import eCommerce.GamesRun.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
