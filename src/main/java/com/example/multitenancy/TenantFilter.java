package com.example.multitenancy;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-TenantID";
    private static final Set<String> ALLOWED_TENANTS = Set.of("tenant1", "tenant2");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

        String tenant = request.getHeader(TENANT_HEADER);
        if (tenant == null || tenant.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing X-TenantID header");
            return;
        }

        if (!ALLOWED_TENANTS.contains(tenant)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid tenant ID");
            return;
        }

        try {
            TenantContext.setCurrentTenant(tenant);
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}


