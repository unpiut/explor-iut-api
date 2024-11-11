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

import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonView;
import explorateurIUT.model.views.AppTextViews;
import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author Julien Fourdan
 */
@Document(collection = "Texte")
public class AppText {

    @JsonView(AppTextViews.Details.class)
    @Id
    private String id;

    @JsonView(AppTextViews.Normal.class)
    @NotBlank
    @Indexed(unique = true)
    private String code;

    @Indexed
    private String language;

    @JsonView(AppTextViews.Normal.class)
    @NotBlank
    private String content;

    private boolean backendMailText;

    protected AppText() {
    }

    public AppText(String code, String content, String language, boolean backendMailText) {
        this.code = code;
        this.content = content;
        this.language = language;
        this.backendMailText = backendMailText;
    }

    public AppText(String code, String content, String language) {
        this(code, content, language, false);
    }

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isBackendMailText() {
        return backendMailText;
    }

    public void setBackendMailText(boolean backendMailText) {
        this.backendMailText = backendMailText;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AppText other = (AppText) obj;
        return Objects.equals(this.id, other.id);
    }

}
