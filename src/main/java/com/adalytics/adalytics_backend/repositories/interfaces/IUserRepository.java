package com.adalytics.adalytics_backend.repositories.interfaces;

import com.adalytics.adalytics_backend.models.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(String id);
    List<User> findByOrganizationId(String organizationId);
}
