package issues;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        setEpicId(epicId);
        setType(IssueType.SUBTASK);
    }

    public Subtask (int id, IssueType type, String title, IssueStatus status, String description, int epicId){
        super(id, type, title, status, description);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", type='" + getType() + '\'' +
                ", epicId='" + getEpicId() + '\'' +
                '}';
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
