package issues;

public class Task {

    private int id = 0;
    private String title = "Empty";
    private String description = "Empty";
    private IssueStatus status = IssueStatus.NONE;
    private IssueType type = IssueType.TASK;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }
    //"id,type,name,status,description,epic"
    public Task(int id, IssueType type, String title, IssueStatus status, String description){
        this.id = id;
        this.type = type;
        this.title = title;
        this.status = status;
        this.description = description;
    }


    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", type='" + getType() + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public IssueType getType() {
        return type;
    }

    public void setType(IssueType type) {
        this.type = type;
    }
}
