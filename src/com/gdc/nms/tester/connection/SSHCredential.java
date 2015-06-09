package com.gdc.nms.tester.connection;

public class SSHCredential extends Credential {

    public SSHCredential() {
        this("localhost", "root", "root", 22);
    }

    public SSHCredential(String hostname, String username, String password, int port) {
        super(hostname, username, password, port);
    }
}