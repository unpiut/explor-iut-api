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
import com.fasterxml.jackson.annotation.JsonView;
import explorateurIUT.model.views.AppTextViews;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

/**
 *
 * @author Julien Fourdan
 */
public class AppText {

    @JsonView(AppTextViews.Details.class)
    private String id; // will contain a ref to code

    @JsonView(AppTextViews.Normal.class)
    @NotBlank
    private String code; // Unique for a language, will be used at id

    private String language;

    @JsonView(AppTextViews.Normal.class)
    @NotBlank
    private String content;

    private boolean backendMailText;

    protected AppText() {
    }

    public AppText(String code, String content, String language, boolean backendMailText) {
        this.id = generateId(code, language);
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

    protected void setCode(String code) {
        this.code = code;
        this.id = generateId(this.code, this.language);
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
        this.id = generateId(this.code, this.language);
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

    private static String generateId(String code, String language) {
        if (code == null) {
            return UUID.randomUUID().toString();
        }
        String ref;
        if (language != null && !language.isBlank()) {
            ref = "txt#" + language + "#" + code;
        } else {
            ref = "txt#default#" + code;
        }
        return UUID.nameUUIDFromBytes(ref.getBytes()).toString();
    }
}
