package com.adalytics.adalytics_backend.repositories.interfaces;

import com.adalytics.adalytics_backend.models.entities.Connector;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IConnectorRepository extends MongoRepository<Connector, String> {
    Optional<List<Connector>> findByOrganizationId(String organizationId);
    Optional<Connector> findByPlatformUserId(String platformUserId);
    String deleteByIdAndOrganizationId(String id, String organizationId);
}
