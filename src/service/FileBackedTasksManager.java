package service;

import issues.*;

import java.io.*;
import java.util.List;

import static issues.IssueType.*;


public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private final String filePath;


    public FileBackedTasksManager(String filePath) {
        super();
        this.filePath = filePath;
    }

    private String issueToString(Task task) {
        StringBuilder sb = new StringBuilder();
        String issueAsString = null;
        if (task.getType() == TASK || task.getType() == EPIC) {
            sb.append(task.getId() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() + "," + task.getDescription());
            issueAsString = sb.toString();
            return issueAsString;
        } else if (task.getType() == SUBTASK) {
            Subtask subtask = (Subtask) task;
            sb.append(subtask.getId() + "," + subtask.getType() + "," + subtask.getTitle() + "," + subtask.getStatus() + "," + subtask.getDescription() + "," + subtask.getEpicId());
            issueAsString = sb.toString();
            return issueAsString;
        }

        return null;
    }

    public void save() {

        StringBuilder sb = new StringBuilder();
        String history;
        history = historyToString(historyManager);
        sb.append("id,type,name,status,description,epic");
        sb.append("\n");

        try {
            for (Task task : tasks.values()) {
                sb.append(issueToString(task));
                sb.append("\n");
            }
            for (Epic epic : epics.values()) {
                sb.append(issueToString(epic));
                sb.append("\n");
            }
            for (Subtask subtask : subtasks.values()) {
                sb.append(issueToString(subtask));
                sb.append("\n");
            }
            sb.append("\n");

            sb.append(history);


            FileWriter fw = new FileWriter(filePath);
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public void loadFromFile() {
        try {
            File file = new File(filePath);
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            reader.readLine();
            String line;
            String lineWithHistory;

            while (true) {
                line = reader.readLine();
                if (line.length() == 0) {
                    lineWithHistory = reader.readLine();
                    restoreHistory(lineWithHistory);
                    break;
                }
                stringToIssue(line);
            }
            reader.close();
            restoreIdCount();
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public void stringToIssue(String line) {
        String[] splittedLine = line.split(",");
        //System.out.println(splittedLine[1]);
        IssueType type = checkType(splittedLine[1]);
        IssueStatus status = checkStatus(splittedLine[3]);
        int id = Integer.parseInt(splittedLine[0]);
        switch (type) {
            case TASK:
                Task task = new Task(id, type, splittedLine[2], status, splittedLine[4]);
                tasks.put(id, task);
                break;
            case EPIC:
                Epic epic = new Epic(id, type, splittedLine[2], status, splittedLine[4]);
                epics.put(id, epic);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(splittedLine[5]);
                Subtask subtask = new Subtask(id, type, splittedLine[2], status, splittedLine[4], epicId);
                subtasks.put(id, subtask);
                break;
        }
    }

    public IssueType checkType(String value) {
        IssueType type = null;
        switch (value) {
            case "TASK":
                type = TASK;
                break;
            case "SUBTASK":
                type = SUBTASK;
                break;
            case "EPIC":
                type = EPIC;
                break;
        }
        return type;
    }

    public IssueStatus checkStatus(String value) {
        IssueStatus status = IssueStatus.NONE;
        switch (value) {
            case "IN_PROGRESS":
                status = IssueStatus.IN_PROGRESS;
                break;
            case "DONE":
                status = IssueStatus.DONE;
                break;
            case "NEW":
                status = IssueStatus.NEW;
                break;
        }
        return status;
    }

    public void restoreIdCount() {
        int idCount = 0;
        for (int id : tasks.keySet()) {
            if (id > idCount) {
                idCount = id;
            }
        }
        for (int id : subtasks.keySet()) {
            if (id > idCount) {
                idCount = id;
            }
        }
        for (int id : epics.keySet()) {
            if (id > idCount) {
                idCount = id;
            }
        }
        this.idCount = idCount;
    }

    public void restoreHistory(String lineWithHistory) {
        String[] savedHistory = lineWithHistory.split(",");
        for (String idAsString : savedHistory) {
            int id = Integer.parseInt(idAsString);
            historyManager.add(getIssueById(id), id);
        }
    }


    static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        List<Task> history = manager.getHistory();
        for (Task task : history) {
            sb.append(task.getId() + ",");
        }
        String historyInLine = sb.toString();
        return historyInLine;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void updateEpicStatus(int id) {
        super.updateEpicStatus(id);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }

    @Override
    public Task getIssueById(int id) {
        super.getIssueById(id);
        save();
        return super.getIssueById(id);
    }

    @Override
    public void deleteIssueById(int id) {
        super.deleteIssueById(id);
        save();
    }

    @Override
    public void removeAllRelatedSubtasks(int epicId) {
        super.removeAllRelatedSubtasks(epicId);
        save();
    }
}

