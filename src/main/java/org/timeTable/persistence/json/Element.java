package org.timeTable.persistence.json;

public class Element {
    private float type;
    private float id;
    private String name;
    private String longName;
    private String displayname;
    private String alternatename;
    private boolean canViewTimetable;
    private float roomCapacity;

    // Getter Methods

    public float getType() {
        return type;
    }

    public float getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLongName() {
        return longName;
    }

    public String getDisplayname() {
        return displayname;
    }

    public String getAlternatename() {
        return alternatename;
    }

    public boolean getCanViewTimetable() {
        return canViewTimetable;
    }

    public float getRoomCapacity() {
        return roomCapacity;
    }
}
