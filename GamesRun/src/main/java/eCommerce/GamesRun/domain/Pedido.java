package eCommerce.GamesRun.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long compradorId;
    private String status, dataCriacao;
    private double valorTotal;

    public Long getId() {
        return id;
    }
    public Long getCompradorId() {
        return compradorId;
    }
    public String getStatus() {
        return status;
    }
    public String getDataCriacao() {
        return dataCriacao;
    }
    public double getValorTotal() {
        return valorTotal;
    }

    public void setCompradorId(Long compradorId) {
        this.compradorId = compradorId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }
}