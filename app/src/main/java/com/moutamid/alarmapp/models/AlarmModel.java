package com.moutamid.alarmapp.models;

public class AlarmModel {
    public String _id, title, source, description, shortDescription, alarmText;
    public boolean enabled;
    public int priority, state, type, __v;

    public AlarmModel() {
    }

    public AlarmModel(String _id, String title, String source, String description, String shortDescription, String alarmText, boolean enabled, int priority, int state, int type, int __v) {
        this._id = _id;
        this.title = title;
        this.source = source;
        this.description = description;
        this.shortDescription = shortDescription;
        this.alarmText = alarmText;
        this.enabled = enabled;
        this.priority = priority;
        this.state = state;
        this.type = type;
        this.__v = __v;
    }
}
