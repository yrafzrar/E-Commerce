package eCommerce.GamesRun.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "comprador_id")
    private Long compradorId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comprador_id", insertable = false, updatable = false, nullable = false)
    private Usuario comprador;
    private String status, dataCriacao;
    private String formaPagamento, enderecoEntrega;
    @Column(length = 4000)
    private String itensResumo;
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
    public String getFormaPagamento() { return formaPagamento; }
    public String getEnderecoEntrega() { return enderecoEntrega; }
    public String getItensResumo() { return itensResumo; }

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
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }
    public void setEnderecoEntrega(String enderecoEntrega) { this.enderecoEntrega = enderecoEntrega; }
    public void setItensResumo(String itensResumo) { this.itensResumo = itensResumo; }
}