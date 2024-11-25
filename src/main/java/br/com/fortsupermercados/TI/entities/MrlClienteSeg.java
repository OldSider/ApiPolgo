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
@Table(name = "mrl_clienteseg")
public class MrlClienteSeg {

    @Id
    private int seqpessoa;

    private int nrosegmento;

    private int nrorepresentante;

    private String nrotabvendaprinc;

    private String periodvisita;

    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dtaativou;

    private String usuativou;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dtaalteracao;

    private String usualteracao;

    private String indreplicacao;
}
