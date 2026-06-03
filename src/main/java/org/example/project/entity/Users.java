package org.example.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.example.project.extra.AbstractEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class Users extends AbstractEntity implements UserDetails {

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<UserRole> role = new HashSet<>();


    private String emailCode;


    private LocalDateTime birthday;


    @Builder.Default
    private boolean enabled = false;

    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (UserRole userRole : role) {
            authorities.add(userRole);

            authorities.addAll(userRole.getRole()
                    .getPermissions());
        }

        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }


}
