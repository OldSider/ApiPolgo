package br.com.fortsupermercados.TI.repositories;

import br.com.fortsupermercados.TI.entities.EnvioClienteLog;
import br.com.fortsupermercados.TI.entities.EnvioDocLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvioClienteLogRepository extends JpaRepository<EnvioClienteLog, String> {
}
