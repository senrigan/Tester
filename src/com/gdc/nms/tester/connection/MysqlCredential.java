package com.gdc.nms.tester.connection;

public class MysqlCredential extends Credential {
    private String dataBaseName;

    public MysqlCredential() {
        this("localhost", "root", "root", "nms", 3306);
    }

    public MysqlCredential(String hostname, String username, String password, String dataBaseName, int port) {
        super(hostname, username, password, port);
        this.dataBaseName = dataBaseName;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    @Override
    public String toString() {
        return "MysqlCredential [dataBaseName=" + dataBaseName + " " + super.toString() + "]";
    }

}
