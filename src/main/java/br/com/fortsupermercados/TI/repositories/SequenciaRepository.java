package br.com.fortsupermercados.TI.repositories;

import br.com.fortsupermercados.TI.entities.GePessoa;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface SequenciaRepository extends JpaRepository<GePessoa, Long> {

    @Query(value = "SELECT COALESCE(MAX(sequencia), 0) + 1 AS next_seq FROM ge_sequencia WHERE TRIM(LOWER(nometabela)) = :tableName", nativeQuery = true)
    int getNextSequence(@Param("tableName") String tableName);
}