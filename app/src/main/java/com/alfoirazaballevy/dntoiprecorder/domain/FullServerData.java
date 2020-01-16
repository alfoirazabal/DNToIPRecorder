package com.alfoirazaballevy.dntoiprecorder.domain;

import java.util.Date;

import dntoiprecorderlib.ServerData;

public class FullServerData extends ServerData {

    private int id;
    private Exception error;

    public FullServerData(int id,  String dateTime, String dnsName, String ip) {
        super(dnsName, ip, new Date(Date.parse(dateTime)));
        this.setId(id);
    }

    public FullServerData(String dateTime, String dnsName, String ip) {
        super(dnsName, ip, new Date(Date.parse(dateTime)));
        this.setId(-1);
        this.setError(null);
    }

    public FullServerData(Exception error, Date dateTime, String dnsName) {
        super(dnsName, "", dateTime);
        this.setId(-1);
        this.setIp(null);
        this.setError(error);
    }

    public static FullServerData convertFromServerData(ServerData serverData) {
        return new FullServerData(
                serverData.getDateTimeStringed(),
                serverData.getDnsName(),
                serverData.getIp()
        );
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Exception getError() { return error; }

    public void setError(Exception error) { this.error = error; }

    public boolean hasError() {
        return (this.getError() != null);
    }

    @Override
    public String toString() {
        String ipMessage;
        if(this.hasError()) {
            ipMessage = this.getError().getMessage();
        } else {
            ipMessage = this.getIp();
        }
        return this.getDateTimeStringed() + "\t" +
                this.getDnsName() + "\t" +
                ipMessage;

    }

}
