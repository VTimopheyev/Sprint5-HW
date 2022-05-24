package service;

import java.util.ArrayList;
import java.util.HashMap;

import issues.*;

public class InMemoryTaskManager implements TaskManager {

    protected int idCount;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

   public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public void addTask(Task task) {
        idCount++;
        task.setId(idCount);
        task.setStatus(IssueStatus.NEW);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            tasks.put(updatedTask.getId(), updatedTask);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        idCount++;
        epic.setId(idCount);
        epic.setStatus(IssueStatus.NEW);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic updatedEpic) {

        if (epics.containsKey(updatedEpic.getId())) {
            epics.put(updatedEpic.getId(), updatedEpic);
        }
    }

    @Override
    public void updateEpicStatus(int id) {
        boolean subtasksInEpic = checkSubtasksInEpic(id);

        if (subtasksInEpic && checkSubtasksAllDoneInEpic(id)) {
            epics.get(id).setStatus(IssueStatus.DONE);
        } else if (!subtasksInEpic || checkSubtasksAllNewInEpic(id)) {
            epics.get(id).setStatus(IssueStatus.NEW);
        } else if (subtasksInEpic && !checkSubtasksAllDoneInEpic(id)) {
            epics.get(id).setStatus(IssueStatus.IN_PROGRESS);
        }
    }

    public boolean checkSubtasksInEpic(int id) {

        for (int key : subtasks.keySet()) {
            if (subtasks.get(key).getEpicId() == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkSubtasksAllDoneInEpic(int id) {

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == id && !(subtask.getStatus()).equals(IssueStatus.DONE)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkSubtasksAllNewInEpic(int id) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == id && !(subtask.getStatus()).equals(IssueStatus.NEW)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        idCount++;
        subtask.setId(idCount);
        subtask.setStatus(IssueStatus.NEW);
        subtasks.put(subtask.getId(), subtask);
        if (epics.get(subtask.getEpicId()).getStatus().equals(IssueStatus.DONE)) {
            epics.get(subtask.getEpicId()).setStatus(IssueStatus.IN_PROGRESS);
        }
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        if (subtasks.containsKey(updatedSubtask.getId())) {
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
        }
        updateEpicStatus(updatedSubtask.getEpicId());
    }

    @Override
    public Task getIssueById(int id) {

        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id), id);
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            historyManager.add(epics.get(id), id);
            return epics.get(id);
        } else if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id), id);
            return subtasks.get(id);
        }
        return null;
    }

    @Override
    public void deleteIssueById(int id) {

        if (tasks.containsKey(id)) {
            historyManager.removeTaskFromHistory(tasks.get(id), id);
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            historyManager.removeTaskFromHistory(epics.get(id), id);
            epics.remove(id);
            removeAllRelatedSubtasks(id);

        } else if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            historyManager.removeTaskFromHistory(subtasks.get(id), id);
            subtasks.remove(id);
            updateEpicStatus(epicId);
        }

    }

    @Override
    public void removeAllRelatedSubtasks(int epicId) {
        ArrayList<Integer> subtasksToDelete = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                subtasksToDelete.add(subtask.getId());
            }
        }
        for (Integer id : subtasksToDelete) {
            historyManager.removeTaskFromHistory(subtasks.get(id), id);
            subtasks.remove(id);
        }
    }
}

