package le.cache.bis.services.impl;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"le.cache"}, 
        excludeFilters= {@ComponentScan.Filter(Configuration.class)})
public class TestConfig {
}
