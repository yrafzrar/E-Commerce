package eCommerce.GamesRun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import eCommerce.GamesRun.domain.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}