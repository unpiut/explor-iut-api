package explorateurIUT.excelImport.model;

public class ExcelAppText {
    private final String code;
    private String content;

    public ExcelAppText(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void format(StringBuilder sb, String padding, int nbPads){
        String pad = padding.repeat(nbPads);
        sb.append(pad).append("code ").append(this.code).append(System.lineSeparator());
        sb.append(pad).append("- texte : ").append(this.content).append(System.lineSeparator());
    }
}
