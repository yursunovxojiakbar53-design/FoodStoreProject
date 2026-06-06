package org.example.project.valid;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.project.exception.ForbiddenException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    @Before("@annotation(requirePermission)")
    public void check(RequirePermission requirePermission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        assert auth != null;
        boolean hasPermission = auth.getAuthorities()
                .stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), requirePermission.value()));

        if (!hasPermission)
            throw new ForbiddenException("Ruxsat yo'q: " + requirePermission.value());
    }
}