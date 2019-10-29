package com.kodekonveyor.tidyup;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TidyUserRepository extends JpaRepository<TidyUser, Long> {
    TidyUser findByEmail(String email);
    Optional<TidyUser> findById(long id);
}
