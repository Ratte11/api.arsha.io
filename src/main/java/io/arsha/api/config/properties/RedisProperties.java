package io.arsha.api.config.properties;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Nullable;
import java.util.Optional;
import lombok.Data;

@Data
public class RedisProperties {

    private Integer port = 6379;

    private String host = "localhost";

    private String password = "";

    /**
     * Redisson expects null when no password is configured.
     */
    @Nullable
    public String getPassword() {
        return Optional.ofNullable(password)
                .filter(StringUtils::isNotBlank)
                .orElse(null);
    }
}
