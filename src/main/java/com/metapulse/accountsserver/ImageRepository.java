package com.metapulse.accountsserver;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ImageRepository extends CrudRepository<Image, Integer> {
    Optional<Image> findFirstByOrderByIdAsc();
}
