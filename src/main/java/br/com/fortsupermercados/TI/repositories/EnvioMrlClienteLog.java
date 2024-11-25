package br.com.fortsupermercados.TI.repositories;

import br.com.fortsupermercados.TI.entities.MrlCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvioMrlClienteLog extends JpaRepository<MrlCliente, String> {
}
