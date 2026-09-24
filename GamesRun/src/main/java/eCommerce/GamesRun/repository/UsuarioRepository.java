package eCommerce.GamesRun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public interface UsuarioRepository extends JpaRepository<Long, Id> {


}