package br.com.fortsupermercados.TI.repositories;



import br.com.fortsupermercados.TI.entities.EnvioVariaveis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvioVariaveisLog extends JpaRepository<EnvioVariaveis, String> {
}
