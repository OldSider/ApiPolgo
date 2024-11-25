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
@Table(name = "mrl_clientecredito")
public class MrlClienteCredito {

    @Id
    private int seqpessoa;

    private int nroformapagto;

    private int vlrlimitecredito;

    private String statuscredito;

    private int empultcadastro;

    private String indprincipal;

    private String indreplicacao;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dtaalteracao;

    private String usualteracao;

    private int vlrimiteparccredito;
}
