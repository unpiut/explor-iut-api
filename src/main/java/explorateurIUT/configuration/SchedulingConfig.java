/*
 * Copyright (C) 2024 IUT Laval - Le Mans Université.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package explorateurIUT.configuration;

import explorateurIUT.security.mailQuota.services.IPQuotaValidator;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 *
 * @author rvenant
 */
@Configuration
@Order(2)
@EnableAsync
@EnableScheduling
public class SchedulingConfig {

    private static final Log LOG = LogFactory.getLog(SchedulingConfig.class);

    @PostConstruct
    protected void init() {
        LOG.info("INIT SCHEDULING CONFIG FOR InMemoryQuotaValidator");
    }

    @Bean
    public ClearCacheCron clearCacheCron(IPQuotaValidator ipQuotaValidator) {
        LOG.info("Create clear cron");
        return new ClearCacheCron(ipQuotaValidator);
    }

    public static class ClearCacheCron {

        private final IPQuotaValidator ipQuotaValidator;

        public ClearCacheCron(IPQuotaValidator ipQuotaValidator) {
            this.ipQuotaValidator = ipQuotaValidator;
        }

        @Scheduled(fixedDelay = 1L, timeUnit = TimeUnit.MINUTES)
        public void clearCaches() {
            LOG.debug("Clear ipQuotaValidator");
            this.ipQuotaValidator.cleanOutdatedQuotas();
        }
    }
}
