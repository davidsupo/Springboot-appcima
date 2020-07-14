package pe.edu.colegiocima.appcima.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.colegiocima.appcima.models.entity.Grado;
import pe.edu.colegiocima.appcima.models.repository.GradoRepository;


@Service
public class GradoServiceImpl implements GradoService {

    @Autowired
    protected GradoRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Iterable<Grado> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Grado findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
