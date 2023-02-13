package com.dlim2012.appuser.repository;

import com.dlim2012.appuser.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

  @Query(
          value = "SELECT * FROM app_user u WHERE u.email = ?1 FOR UPDATE",
          nativeQuery = true
  )
  Optional<AppUser> findByEmailForUpdate(String email);

  Optional<AppUser> findByEmail(String email);

}
