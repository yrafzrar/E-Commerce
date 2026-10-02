package eCommerce.GamesRun.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;

@Entity
public class AvaliacaoVendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "avaliador_id")
    private Long avaliadorId;
    @Column(name = "vendedor_id")
    private Long vendedorId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "avaliador_id", insertable = false, updatable = false, nullable = false)
    private Usuario avaliador;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendedor_id", insertable = false, updatable = false, nullable = false)
    private Usuario vendedor;
    private int nota;
    private String comentario;

    public Long getId() {
        return id;
    }
    public Long getAvaliadorId() {
        return avaliadorId;
    }
    public Long getVendedorId() {
        return vendedorId;
    }
    public int getNota() {
        return nota;
    }
    public String getComentario() {
        return comentario;
    }

    public void setAvaliadorId(Long avaliadorId) {
        this.avaliadorId = avaliadorId;
    }

    public void setVendedorId(Long vendedorId) {
        this.vendedorId = vendedorId;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}