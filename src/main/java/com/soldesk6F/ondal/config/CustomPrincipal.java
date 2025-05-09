package com.soldesk6F.ondal.config;

import java.security.Principal;
import java.util.UUID;

public class CustomPrincipal implements Principal {
    private final UUID userUuid;
    private final String username;

    public CustomPrincipal(UUID userUuid, String username) {
        this.userUuid = userUuid;
        this.username = username;
    }

    @Override
    public String getName() {
        return userUuid.toString(); // 또는 username
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public String getUsername() {
        return username;
    }
}
