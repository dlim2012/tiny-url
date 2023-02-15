package com.dlim2012.appuser.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class AppUser implements UserDetails {

  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private Integer id;
  @Column(name = "firstname", length = 50)
  private String firstname;
  @Column(name = "lastname", length = 50)
  private String lastname;
  @Column(name = "email", length = 100, nullable = false)
  private String email;
  @Column(name = "password", nullable = false)
  private String password;
  @Column(name = "available_short_url")
  private int availableShortUrl;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private Set<ShortUrlPathEntity> shortUrlPathEntities = new HashSet<>();

  public Set<ShortUrlPathEntity> getShortUrlPathEntities() {
    return shortUrlPathEntities;
  }

  public int getAvailableShortUrl() {
    return availableShortUrl;
  }

  public void setAvailableShortUrl(int availableShortUrl) {
    this.availableShortUrl = availableShortUrl;
  }

  @Enumerated(EnumType.STRING)
  private AppUserRole appUserRole;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(appUserRole.name()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String toString() {
    return "AppUser{" +
            "id=" + id +
            ", firstname='" + firstname + '\'' +
            ", lastname='" + lastname + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", availableShortUrl=" + availableShortUrl +
            ", role=" + appUserRole +
            '}';
  }
}
