package br.com.fortsupermercados.TI.services;

import br.com.fortsupermercados.TI.repositories.SequenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SequenciaService {

    @Autowired
    private SequenciaRepository sequenciaRepository;

    public int getNextSequence(String tableName) {
        return sequenciaRepository.getNextSequence(tableName);
    }
}