package eCommerce.GamesRun.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String produto, descricao;
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


   
}
