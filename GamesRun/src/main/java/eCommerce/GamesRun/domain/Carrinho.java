package eCommerce.GamesRun.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        name = "uk_carrinho_usuario_anuncio",
        columnNames = { "usuario_id", "anuncio_id" }))
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "usuario_id")
    private Long usuarioId;
    @Column(name = "anuncio_id")
    private Long anuncioId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false, nullable = false)
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anuncio_id", insertable = false, updatable = false, nullable = false)
    private Anuncio anuncio;
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