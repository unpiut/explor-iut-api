package explorateurIUT.model;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonView;

import explorateurIUT.model.views.AppTextViews;
import jakarta.validation.constraints.NotBlank;

@Document(collection = "Texte", language = "french")
public class AppText {
    @JsonView(AppTextViews.Details.class)
    @Id
    private String id;

    @JsonView(AppTextViews.Normal.class)
    @NotBlank
    private String code;

    @Indexed
    private String language;

    @JsonView(AppTextViews.Normal.class)
    @NotBlank
    private String content;

    protected AppText(){
    }

    public AppText(String code, String content) {
        this.code = code;
        this.content = content;
        this.language = "fr";
    }

    public AppText(String code, String content, String language) {
        this.code = code;
        this.content = content;
        this.language = language;
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
