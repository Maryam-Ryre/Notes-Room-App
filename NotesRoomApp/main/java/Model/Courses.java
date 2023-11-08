package Model;

public class Courses {

    private String cname, description, courseid, image;

    public Courses() {

    }

    public Courses(String cname, String description, String courseid, String image) {

        this.cname = cname;
        this.description = description;
        this.courseid = courseid;
        this.image = image;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
