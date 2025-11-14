package com.qualifygym.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qualifygym.usuarios.model.Rol;

@Repository
public interface RoleRepository extends JpaRepository<Rol, Long> {

}

