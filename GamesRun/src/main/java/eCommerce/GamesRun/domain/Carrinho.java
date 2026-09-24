package eCommerce.GamesRun.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long usuarioId, anuncioId;
    private int quantidade = 1;

    public Long getId() {
        return id;
    }
    public Long getUsuarioId() {
        return usuarioId;
    }
    public Long getAnuncioId() {
        return anuncioId;
    }
    public int getQuantidade() {
        return quantidade;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void setAnuncioId(Long anuncioId) {
        this.anuncioId = anuncioId;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}