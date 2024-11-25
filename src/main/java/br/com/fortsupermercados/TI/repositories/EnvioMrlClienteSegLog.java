package br.com.fortsupermercados.TI.repositories;

import br.com.fortsupermercados.TI.entities.MrlClienteSeg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvioMrlClienteSegLog extends JpaRepository<MrlClienteSeg, String> {
}
