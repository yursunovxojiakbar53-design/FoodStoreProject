package org.example.project.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.example.project.extra.AbstractEntity;
import org.springframework.security.core.GrantedAuthority;

@EqualsAndHashCode(callSuper = true)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole extends AbstractEntity implements GrantedAuthority {

    @Enumerated(EnumType.STRING)
    private org.example.project.enums.Role role;

    @Override
    public String getAuthority() {
        String roleName = role.name();
        return roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
    }
}
