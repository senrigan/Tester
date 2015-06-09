package com.gdc.nms.tester.connection;

public class Credential {
    private String hostname;
    private String username;
    private String password;
    private int port;

    public Credential() {
    }

    public Credential(String hostname, String username, String password, int port) {
        super();
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "hostname=" + hostname + ", username=" + username + ", password=" + password + ", port=" + port;
    }
}
