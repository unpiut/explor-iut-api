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

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author rvenant
 */
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    private boolean devCors = false;

    @NotBlank
    private String adminUsername;

    @NotBlank
    private String adminPassword;

    private boolean csrf = false;

    @Min(1)
    private int maxMailRequestsMinute = 5;

    @Min(1)
    private int maxMailIpRequestsHour = 5;

    @Min(1)
    private int maxMailIpRequestsDeptHour = 5;

    public AppSecurityProperties() {
    }

    public boolean isDevCors() {
        return devCors;
    }

    public void setDevCors(boolean devCors) {
        this.devCors = devCors;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public boolean isCsrf() {
        return csrf;
    }

    public void setCsrf(boolean csrf) {
        this.csrf = csrf;
    }

    public int getMaxMailRequestsMinute() {
        return maxMailRequestsMinute;
    }

    public void setMaxMailRequestsMinute(int maxMailRequestsMinute) {
        this.maxMailRequestsMinute = maxMailRequestsMinute;
    }

    public int getMaxMailIpRequestsHour() {
        return maxMailIpRequestsHour;
    }

    public void setMaxMailIpRequestsHour(int maxMailIpRequestsHour) {
        this.maxMailIpRequestsHour = maxMailIpRequestsHour;
    }

    public int getMaxMailIpRequestsDeptHour() {
        return maxMailIpRequestsDeptHour;
    }

    public void setMaxMailIpRequestsDeptHour(int maxMailIpRequestsDeptHour) {
        this.maxMailIpRequestsDeptHour = maxMailIpRequestsDeptHour;
    }

}
