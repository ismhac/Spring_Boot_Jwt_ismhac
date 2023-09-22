package com.kocobiet.project.repositories;

import com.kocobiet.project.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
