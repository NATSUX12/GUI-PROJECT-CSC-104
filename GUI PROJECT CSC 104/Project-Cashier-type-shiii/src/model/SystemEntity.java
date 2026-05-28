package model;

public abstract class SystemEntity {
    private int id;

    public SystemEntity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getFormattedId() {
        return "ID-" + id;
    }

    public abstract String getDisplayName();
}