package net.vojko.paurus.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "app")
public interface AppConfig {
    int maxBatchSize();
    boolean useDelay();
}
