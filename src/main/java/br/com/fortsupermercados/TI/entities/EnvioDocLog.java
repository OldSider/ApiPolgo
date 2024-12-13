package br.com.fortsupermercados.TI.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import java.util.Date;

@Entity
@Data // Getter e Setter
@NoArgsConstructor // Constructor sem argumentos
@AllArgsConstructor // Contructor com argumentos
@Table(name = "testes_integracao_campanha_natal")
public class EnvioDocLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid2")
    @Column(name = "uuid", updatable = false, nullable = false)
    private String uuid;

    @Column(columnDefinition = "CLOB")
    private String requestJson;

    @Column(columnDefinition = "CLOB")
    private String responseJson;

    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;

    @Column
    private Date horarioInicio;

    @Column
    private Date horarioFim;
}
