package com.gdc.nms.tester.server;

import jade.core.AID;
import jade.core.ProfileImpl;
import jade.util.leap.Properties;
import jade.wrapper.StaleProxyException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gdc.nms.tester.common.AgentsManager;
import com.gdc.nms.tester.common.AgentsUtil;

public class Server {

    /**
     * JADE manager
     */
    private AgentsManager manager;

    /**
     * Address of client
     */
    private AID clientAID;

    /**
     * Address used for JADE Message Transport Protocol
     */
    private String mtpAddress;

    /**
     * Address that client use to connect using SSH
     */
    private String address;

    /**
     * Thread-pool used by ServerAgent
     */
    private ExecutorService workPool;

    /**
     * JADE properties.
     */
    private Properties props;

    /**
     * Server agent
     */
    private ServerAgent agent;

    public Server(String address, String mtpAddress) {
        manager = AgentsManager.instance();
        workPool = Executors.newSingleThreadExecutor();
        this.address = address;
        this.mtpAddress = mtpAddress;
        agent = new ServerAgent(workPool, address, mtpAddress);
        props = new Properties();
        props.setProperty(ProfileImpl.PLATFORM_ID, AgentsUtil.getServerPlaform(mtpAddress));
        props.setProperty(ProfileImpl.MTPS, AgentsUtil.getMTP(mtpAddress));
    }

    public void start() {
        agent.setClientAID(clientAID);
        manager.start(new ProfileImpl(props));
        try {
            manager.addAgent(AgentsUtil.SERVER_AGENT_NAME, agent);
        } catch (StaleProxyException e) {
            stop();
            System.exit(1);
        }
    }

    public void stop() {
        workPool.shutdownNow();
        manager.stop();
    }

    public AID getClientAID() {
        return clientAID;
    }

    public void setClientAID(AID clientAID) {
        this.clientAID = clientAID;
    }

    public String getMtpAddress() {
        return mtpAddress;
    }

    public void setMtpAddress(String mtpAddress) {
        this.mtpAddress = mtpAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ExecutorService getWorkPool() {
        return workPool;
    }

    public void setWorkPool(ExecutorService workPool) {
        this.workPool = workPool;
    }

}
