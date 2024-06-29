package com.adalytics.adalytics_backend.repositories.interfaces;

import com.adalytics.adalytics_backend.models.entities.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrganizationRepository extends MongoRepository<Organization, String> {}
