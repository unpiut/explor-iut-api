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
package explorateurIUT.services.mailManagement;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Remi Venant
 */
@Configuration
@ConfigurationProperties(prefix = "app.mail")
public class MailSendingProperties {

    @NotNull
    @Email
    private String fromAddress;

    @Email
    private String noReplyAddress;

    @NotEmpty
    private String tokenSecret = "change-me";

    @Email
    private String testingMailAddress;

    private int maxAttachementNumber = 3;

    private int maxAttachementsTotalSizeMB = 10;

    private int maxAttachementSizeMB = 10;

    public MailSendingProperties() {
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getNoReplyAddress() {
        return noReplyAddress;
    }

    public void setNoReplyAddress(String noReplyAddress) {
        this.noReplyAddress = noReplyAddress;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public String getTestingMailAddress() {
        return testingMailAddress;
    }

    public void setTestingMailAddress(String testingMailAddress) {
        this.testingMailAddress = testingMailAddress;
    }

    public int getMaxAttachementNumber() {
        return maxAttachementNumber;
    }

    public void setMaxAttachementNumber(int maxAttachementNumber) {
        this.maxAttachementNumber = maxAttachementNumber;
    }

    public int getMaxAttachementsTotalSizeMB() {
        return maxAttachementsTotalSizeMB;
    }

    public void setMaxAttachementsTotalSizeMB(int maxAttachementsTotalSizeMB) {
        this.maxAttachementsTotalSizeMB = maxAttachementsTotalSizeMB;
    }

    public int getMaxAttachementSizeMB() {
        return maxAttachementSizeMB;
    }

    public void setMaxAttachementSizeMB(int maxAttachementSizeMB) {
        this.maxAttachementSizeMB = maxAttachementSizeMB;
    }

}
