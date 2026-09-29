package eCommerce.GamesRun.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import eCommerce.GamesRun.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByNick(String nick);
}