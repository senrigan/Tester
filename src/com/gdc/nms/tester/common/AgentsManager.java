package com.gdc.nms.tester.common;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AgentsManager {

    /**
     * Singleton instance
     */
    private static final AgentsManager INSTANCE = new AgentsManager();

    private Runtime runtime;
    private AgentContainer container;
    private boolean running;

    /**
     * Avoid external instances
     */
    private AgentsManager() {
        runtime = Runtime.instance();
        running = false;
    }

    /**
     * Return AgentsManager instance.
     * 
     * @return AgentsManager instance.
     */
    public static AgentsManager instance() {
        return INSTANCE;
    }

    /**
     * Start Agents Platform with a specify profile
     * 
     * @param profile
     * @throws IllegalStateException
     *             if agents container is already started.
     */
    public void start(Profile profile) throws IllegalStateException {
        try {
            if (Util.isBusyPort(InetAddress.getLocalHost().getHostAddress(), 1099)) {
                throw new IllegalStateException("JADE is already running by another process.");
            }
            container = runtime.createMainContainer(profile);
            if (container == null) {
                throw new IllegalStateException("JADE is already running.");
            }
            running = true;
        } catch (UnknownHostException e) {
        }
    }

    /**
     * Try add a new Agent into Agents container
     * 
     * @param name
     *            Agent's name.
     * @param agent
     *            Agent instance.
     * @throws StaleProxyException
     *             If the underlying agent is dead or gone.
     */
    public void addAgent(String name, Agent agent) throws StaleProxyException {
        container.acceptNewAgent(name, agent).start();
    }

    /**
     * Try to kill a existent agent.
     * 
     * @param name
     *            agent to kill.
     * @throws ControllerException
     *             If the underlying agent is dead or gone
     */
    public void killAgent(String name) throws ControllerException {
        if (name == null || name.isEmpty() || name.equalsIgnoreCase("ams") || name.equalsIgnoreCase("df")) {
            return;
        }
        AgentController agent = container.getAgent(name);
        agent.kill();
    }

    /**
     * Running agents container state.
     * 
     * @return running state.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Stop agents container if running state is true.
     */
    public void stop() {
        if (isRunning()) {
            try {
                container.getPlatformController().kill();
            } catch (ControllerException e) {
                e.printStackTrace();
            }
            runtime.shutDown();
            running = false;
        }
    }

}