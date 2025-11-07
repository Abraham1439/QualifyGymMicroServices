package com.QualifyGym.tema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.QualifyGym.tema.model.Tema;

@Repository
public interface TemaRepository extends JpaRepository<Tema, Long> {

}
