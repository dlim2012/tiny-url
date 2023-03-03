package com.dlim2012.appuser.repository;

import com.dlim2012.appuser.entity.AppUser;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Transactional
  @Query(
          value = "SELECT a FROM AppUser a WHERE a.email = ?1",
          nativeQuery = false
  )
  Optional<AppUser> findByEmailForUpdate(String email);


  @Query(
          value = "SELECT * FROM app_user u WHERE u.email = ?1",
          nativeQuery = true
  )
  Optional<AppUser> findByEmail(String email);
}
