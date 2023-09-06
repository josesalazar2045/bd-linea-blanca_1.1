package com.example.application.data.service;

import com.example.application.data.entity.LineaBlanca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LineaBlancaRepository extends JpaRepository<LineaBlanca, Long>, JpaSpecificationExecutor<LineaBlanca> {

}
