package br.com.fortsupermercados.TI.repositories;


import br.com.fortsupermercados.TI.entities.MrlClienteCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvioMrlClienteCreditoLog extends JpaRepository<MrlClienteCredito, String> {
}
