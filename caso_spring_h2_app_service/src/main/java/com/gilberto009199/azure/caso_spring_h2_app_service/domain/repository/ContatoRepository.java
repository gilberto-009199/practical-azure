package com.gilberto009199.azure.caso_spring_h2_app_service.domain.repository;

import com.gilberto009199.azure.caso_spring_h2_app_service.domain.entity.Contato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContatoRepository extends JpaRepository<Contato, Long> {
}
