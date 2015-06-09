package com.gdc.nms.tester.connection;

/**
 * Object used to save the server information such SSH Credential, MySql
 * Credential, JADE Host etc...
 * 
 * @author Samuel Macias <samuel.macias@gdc-cala.com.mx>
 *
 */
public class ServerObject {
    /**
     * Name Server
     */
	private String nameServer;

	/**
     * SSH Credential
     */
    private SSHCredential sshCredential;
    /**
     * MySql Credential
     */
    private MysqlCredential mysqlCredential;

    /**
     * JADE Host address
     */
    private String jadeHost;

    /**
     * Glassfish path ex. (/opt/glassfish/).
     */
    private String glassFishPath;

    /**
     * All projects in XML format.
     */
    private String projects;

    public ServerObject() {
        sshCredential = new SSHCredential();
        mysqlCredential = new MysqlCredential();
        jadeHost = "";
        glassFishPath = "/opt/glassfish/";
        projects = "";
    }

    public SSHCredential getSshCredential() {
        return sshCredential;
    }

    public void setSshCredential(SSHCredential sshCredential) {
        this.sshCredential = sshCredential;
    }

    public MysqlCredential getMysqlCredential() {
        return mysqlCredential;
    }

    public void setMysqlCredential(MysqlCredential mysqlCredential) {
        this.mysqlCredential = mysqlCredential;
    }

    public String getJadeHost() {
        return jadeHost;
    }

    public void setJadeHost(String jadeHost) {
        this.jadeHost = jadeHost;
    }

    public String getGlassfishPath() {
        return glassFishPath;
    }

    public void setGlassfishPath(String glassfishPath) {
        this.glassFishPath = glassfishPath;
    }

    public String getProjects() {
        return projects;
    }

    public void setProjects(String projects) {
        this.projects = projects;
    }
    
    
	@Override
	public String toString() {
		return "ServerObject [nameServer=" + nameServer + ", sshCredential="
				+ sshCredential + ", mysqlCredential=" + mysqlCredential
				+ ", jadeHost=" + jadeHost + ", glassFishPath=" + glassFishPath
				+"]";
	}

	public String getNameServer() {
		return nameServer;
	}

	public void setNameServer(String nameServer) {
		this.nameServer = nameServer;
	}
}
