package com.example.warehouse.auth.repository;


import com.example.warehouse.auth.models.TokenDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends MongoRepository<TokenDocument, String> {
    @Query("{ 'user': ?0, 'revoked': false, 'expired': false }")
    List<TokenDocument> findAllValidTokensByUsername(String username);
    Optional<TokenDocument> findByToken(String token);
}
