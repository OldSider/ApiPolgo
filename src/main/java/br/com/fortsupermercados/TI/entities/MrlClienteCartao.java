package br.com.fortsupermercados.TI.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mrl_clientecartao")
public class MrlClienteCartao {

    private int nrocartao;

    private int nroformapagto;

    @Id
    private int seqpessoatitular;

    private int seqpessoaportador;

    private String indemissaocartao;

    private String statuscartao;

    private int empultcadastro;

    private String indreplicacao;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dtageracao;

    private String usugeracao;

    private String indcompoerenda;
}
