package com.QualifyGym.tema.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.QualifyGym.tema.repository.TemaRepository;
import com.QualifyGym.tema.webpublicacion.PublicacionCat;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TemaService {
    
    @Autowired
    private TemaRepository temaRepository;
    @Autowired
    private PublicacionCat publicacionCat;

    

}
