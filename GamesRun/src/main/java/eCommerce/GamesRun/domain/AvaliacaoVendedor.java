package eCommerce.GamesRun.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AvaliacaoVendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long avaliadorId, vendedorId;
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