package com.example.application.data.service;

import com.example.application.data.entity.LineaBlanca;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class LineaBlancaService {

    private final LineaBlancaRepository repository;

    public LineaBlancaService(LineaBlancaRepository repository) {
        this.repository = repository;
    }

    public Optional<LineaBlanca> get(Long id) {
        return repository.findById(id);
    }

    public LineaBlanca update(LineaBlanca entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<LineaBlanca> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<LineaBlanca> list(Pageable pageable, Specification<LineaBlanca> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
