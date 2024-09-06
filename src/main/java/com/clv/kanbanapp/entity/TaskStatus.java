package com.clv.kanbanapp.entity;

public enum TaskStatus {
    TODO("ToDo"),
    IN_PROGRESS("In Progress"),
    DONE("Done"),
    CANCEL("Cancel");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
