/*
 * Copyright (C) 2023 IUT Laval - Le Mans Université.
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
package explorateurIUT.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

/**
 * 
 * @author Julien Fourdan
 */
@Document(collection="PendingMail")
public class PendingMail {
    @Id
    private String Id;

    @NotEmpty
    private List<String> emailIUT;

    @NotBlank
    private String subject;

    @NotBlank
    private String body;

    @NotBlank
    private String replyTo;

    @CreatedDate
    private LocalDateTime createdDateTime;

    protected PendingMail(){
    }

    public PendingMail(List<String> emailIUT, String subject, String body, String replyTo){
        this.emailIUT = emailIUT;
        this.subject = subject;
        this.body = body;
        this.replyTo = replyTo;
    }

    public String getId() {
        return Id;
    }

    protected void setId(String id) {
        Id = id;
    }

    public List<String> getEmailIUT() {
        return emailIUT;
    }

    public void setEmailIUT(List<String> emailIUT) {
        this.emailIUT = emailIUT;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    protected void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }
}
