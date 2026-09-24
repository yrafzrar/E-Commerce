package eCommerce.GamesRun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import eCommerce.GamesRun.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {


}