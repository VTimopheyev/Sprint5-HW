package issues;

public class Epic extends Task {

    public Epic(String title, String description) {
        super(title, description);
        setType(IssueType.EPIC);
    }

    public Epic(int id, IssueType type, String title, IssueStatus status, String description){
        super(id, type, title, status, description);
    }


    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", type='" + getType() + '\'' +
                '}';
    }

}
