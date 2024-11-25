package br.com.fortsupermercados.TI.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data // Getter e Setter
@NoArgsConstructor // Constructor sem argumentos
@AllArgsConstructor // Contructor com argumentos
@Table(name = "testes_integracao_campanha_variaveis")
public class EnvioVariaveis {

    @Id
    private int id;

    private int seqpessoa;

    private String nomerazao;

    private String nrocgcpf;

    private String digcgcpf;

    private int nroformapagto;

    private int vlrlimitecredito;

    private String nrocartao;

    private int seqpessoatitular;

    private int seqpessoaportador;
}
