package com.jhonystein.pedidex.model;

import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "PRODUTOS")
@SequenceGenerator(name = "PRODUTOS_SEQ", sequenceName = "PRODUTOS_SEQ", allocationSize = 1)
public class Produto implements Entidade {
    
    @Id
    @Column(name = "ID_PRODUTO")
    @GeneratedValue(generator = "PRODUTOS_SEQ", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull(message = "{Produto.codigo.NotNull}")
    @Size(min = 2, max = 20, message = "{Produto.codigo.Size}")
    @Column(name = "CODIGO", length = 20, unique = true)
    private String codigo;
    
    @Size(max = 255, message = "{Produto.descricao.Size}")
    @Column(name = "DESCRICAO", length = 255)
    private String descricao;
    
    @NotNull(message = "{Produto.preco.NotNull}")
    @Digits(integer = 10, fraction = 5, message = "{Produto.preco.Digits}")
    @Column(name = "PRECO", precision = 15, scale = 5)
    private BigDecimal preco;
    
    @NotNull(message = "{Produto.quantidade.NotNull}")
    @Digits(integer = 10, fraction = 5, message = "{Produto.quantidade.Digits}")
    @Column(name = "ESTOQUE", precision = 15, scale = 5)
    private BigDecimal quantidade;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.id);
        hash = 23 * hash + Objects.hashCode(this.codigo);
        hash = 23 * hash + Objects.hashCode(this.descricao);
        hash = 23 * hash + Objects.hashCode(this.preco);
        hash = 23 * hash + Objects.hashCode(this.quantidade);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Produto other = (Produto) obj;
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        if (!Objects.equals(this.descricao, other.descricao)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.preco, other.preco)) {
            return false;
        }
        if (!Objects.equals(this.quantidade, other.quantidade)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Produto{" + "id=" + id + ", codigo=" + codigo + ", descricao=" + descricao + ", preco=" + preco + ", quantidade=" + quantidade + '}';
    }
    
}
