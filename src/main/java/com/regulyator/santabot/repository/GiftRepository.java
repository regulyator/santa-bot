package com.regulyator.santabot.repository;

import com.regulyator.santabot.domain.entity.Gift;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GiftRepository extends MongoRepository<Gift, String> {

    boolean existsByUserId(String userId);

    Optional<Gift> deleteByUserId(String userId);

    Optional<Gift> getByUserId(String userId);

    @Query("{ 'draw' : false }")
    List<Gift> getAllNotDraw();
}
