package com.adalytics.adalytics_backend.repositories.interfaces;

import com.adalytics.adalytics_backend.models.entities.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends MongoRepository<Users, String> {
}
