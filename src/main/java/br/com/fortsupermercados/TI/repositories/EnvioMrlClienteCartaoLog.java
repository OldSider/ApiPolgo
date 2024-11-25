package br.com.fortsupermercados.TI.repositories;


import br.com.fortsupermercados.TI.entities.MrlClienteCartao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnvioMrlClienteCartaoLog extends JpaRepository<MrlClienteCartao, String> {
}
