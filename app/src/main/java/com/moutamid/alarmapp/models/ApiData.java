package com.moutamid.alarmapp.models;

public class ApiData {
    public String link, clientId,clientSecret;

    public ApiData() {}

    public ApiData(String link, String clientId, String clientSecret) {
        this.link = link;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
}
