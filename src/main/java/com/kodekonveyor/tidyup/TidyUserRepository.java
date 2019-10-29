package com.kodekonveyor.tidyup;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TidyUserRepository extends JpaRepository<TidyUser, Long> {
    TidyUser findByEmail(String email);
}
