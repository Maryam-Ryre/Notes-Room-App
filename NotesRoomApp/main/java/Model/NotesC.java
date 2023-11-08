package Model;

public class NotesC {

    private String name, pdf;

    public NotesC() {
    }

    public NotesC(String name, String pdf) {
        this.name = name;
        this.pdf = pdf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }
}
