package eCommerce.GamesRun.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String produto, descricao, estadoConservacao;
    private Long categoriaId, vendedorId;
    @Column(length = 2000)
    private String imagemUrl;
    private double preco;


    public Long getId() {
        return id;
    }
    public String getProduto() {
        return produto;
    }
    public String getDescricao() {
        return descricao;
    }
    public double getPreco() {
        return preco;
    }
    public String getEstadoConservacao() { return estadoConservacao; }
    public String getImagemUrl() { return imagemUrl; }
    public Long getCategoriaId() { return categoriaId; }
    public Long getVendedorId() { return vendedorId; }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public void setEstadoConservacao(String estadoConservacao) { this.estadoConservacao = estadoConservacao; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public void setVendedorId(Long vendedorId) { this.vendedorId = vendedorId; }
}