package com.corely.corely_backend.repository;

import com.corely.corely_backend.entity.InvalidatedToken;
import com.corely.corely_backend.entity.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
    void deleteByUser(User user);

    List<InvalidatedToken> findByUser(User user);
}
