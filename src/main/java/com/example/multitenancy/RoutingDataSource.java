package com.example.multitenancy;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        String tenant = TenantContext.getCurrentTenant();
        if (tenant == null) {
            throw new IllegalStateException("No tenant specified in request");
        }
        return tenant;
    }
}
