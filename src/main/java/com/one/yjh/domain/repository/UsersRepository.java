package com.one.yjh.domain.repository;

import com.one.yjh.domain.entity.Users;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long>{
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
}
