package com.adalytics.adalytics_backend.repositories.interfaces;

import com.adalytics.adalytics_backend.models.entities.Connector;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IConnectorRepository extends MongoRepository<Connector, String> {
    Optional<Connector> findById(String id);
    Optional<List<Connector>> findByUserId(String userId);
    void deleteById(String id);
}
