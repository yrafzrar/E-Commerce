package eCommerce.GamesRun.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nome, nick, senha;
    private int idade, cpf;


    public Long getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public String getNick() {
        return nick;
    }
    public String getSenha() {
        return senha;
    }
    public int getIdade() {
        return idade;
    }
    public int getCpf() {
        return cpf;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public void setCpf(int cpf) {
        this.cpf = cpf;
    }
}