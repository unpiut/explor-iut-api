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
    private String texte;

    protected AppText(){
    }

    public AppText(String code, String texte) {
        this.code = code;
        this.texte = texte;
        this.language = "fr";
    }

    public AppText(String code, String texte, String language) {
        this.code = code;
        this.texte = texte;
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

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
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
