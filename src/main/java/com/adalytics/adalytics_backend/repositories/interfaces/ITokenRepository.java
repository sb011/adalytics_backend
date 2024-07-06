package com.adalytics.adalytics_backend.repositories.interfaces;

import com.adalytics.adalytics_backend.models.entities.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITokenRepository extends MongoRepository<Token, String> {
    Optional<Token> findByToken(String token);
}