package explorateurIUT.model;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonView;

import explorateurIUT.model.views.TexteViews;
import jakarta.validation.constraints.NotBlank;

@Document(collection = "Texte", language = "french")
public class Texte {
    @JsonView(TexteViews.Normal.class)
    @Id
    private String id;

    @JsonView(TexteViews.Normal.class)
    @NotBlank
    private String code;

    @JsonView(TexteViews.Normal.class)
    @NotBlank
    private String texte;

    protected Texte(){
    }

    public Texte(String code, String texte) {
        this.code = code;
        this.texte = texte;
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
        final Texte other = (Texte) obj;
        return Objects.equals(this.id, other.id);
    }
    
}
