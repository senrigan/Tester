package com.gdc.nms.tester.client;

import jade.core.AID;
import jade.core.ProfileImpl;
import jade.util.leap.Properties;
import jade.wrapper.StaleProxyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.gdc.nms.model.Device;
import com.gdc.nms.model.DeviceResource;
import com.gdc.nms.model.Interface;
import com.gdc.nms.model.IpSla;
import com.gdc.nms.model.QosClass;
import com.gdc.nms.tester.client.ssh.SSHConnector;
import com.gdc.nms.tester.common.AgentsManager;
import com.gdc.nms.tester.common.AgentsUtil;
import com.gdc.nms.tester.common.exception.EmptyException;
import com.gdc.nms.tester.common.exception.StartException;
import com.gdc.nms.tester.common.response.DeviceInterfacesResponse;
import com.gdc.nms.tester.common.response.DeviceIpSlasResponse;
import com.gdc.nms.tester.common.response.DeviceQousResponce;
import com.gdc.nms.tester.common.response.DeviceResourcesResponse;
import com.gdc.nms.tester.common.response.SqlInsertableRowDescriptorResponse;
import com.gdc.nms.tester.connection.SSHCredential;
import com.gdc.nms.tester.connection.ServerObject;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Client {

    /**
     * Servers discovered in {@link ClientAgent#discovery(int)}
     */
    private Map<String, AID> availableServers;

    /**
     * ClientAgent used for communication with servers
     */
    private ClientAgent agent;

    /**
     * Properties to start jade container
     */
    private Properties props;

    /**
     * Jade Agents Manager
     */
    private AgentsManager manager;

    /**
     * Self reference of Agent Identification
     */
    private AID self;

    /**
     * server-client.jar file path
     */
    private File serverTesterFile;

    /**
     * List of libraries used by serverTesterFile
     */
    private File[] libraries;

    /**
     * Server credentials and stuff.
     */
    private List<ServerObject> servers;

    /**
     * Server successful started.
     */
    private List<ServerObject> startedServers;

    /**
     * All connectors for the started servers.
     */
    private List<SSHConnector> connectors;

    /**
     * Client state
     */
    private boolean running;

    /**
     * Constructor
     */
    public Client(List<ServerObject> servers) {
        validServers(servers);
        availableServers = Collections.synchronizedMap(new HashMap<String, AID>());
        agent = new ClientAgent(availableServers);
//        connectors = new ArrayList<SSHConnector>(servers.size());
        connectors = Collections.synchronizedList(new ArrayList<SSHConnector>(servers.size()));
        manager = AgentsManager.instance();
        props = new Properties();
        this.servers = servers;
    }

    /**
     * Validate list of servers.
     * 
     * @param servers
     */
    private void validServers(List<ServerObject> servers) {
        if (servers == null) {
            throw new NullPointerException("Servers list must not be null.");
        } else if (servers.isEmpty()) {
            throw new RuntimeException("Servers list must not be empty.");
        }
    }

    /**
     * Getting all IPv4 network addresses for use as MTP.
     * 
     * @return List of Network addresses
     * @throws SocketException
     */
    private String getInterfaces() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            if (iface.isLoopback() || !iface.isUp()) {
                continue;
            } else {
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Start JADE
     * 
     * @throws SocketException
     *             if some error occurs while getting interfaces.
     * @throws StaleProxyException
     *             if some error occurs while add agent.
     */
    private void startJade() throws SocketException, StaleProxyException {
        props.put(ProfileImpl.PLATFORM_ID, AgentsUtil.CLIENT_PLATFORM_ID);
        props.put(ProfileImpl.MTPS, AgentsUtil.getMTP(getInterfaces()));
        manager.start(new ProfileImpl(props));
        manager.addAgent(AgentsUtil.CLIENT_AGENT_NAME, agent);
        self = agent.getAID();
        for (String address : agent.getAMS().getAddressesArray()) {
            self.addAddresses(address);
        }
    }

    /**
     * 
     * @throws FileNotFoundException
     *             if serverTesterFile does not exist or is not a file, or if
     *             libraries directory not exist or is not a directory.
     */
    private void validateServerTesterFile() throws FileNotFoundException {
        if (serverTesterFile == null) {
            throw new NullPointerException("Server tester file was not set.");
        } else if (serverTesterFile.exists() && serverTesterFile.isFile()) {
            File librariesDir = new File(serverTesterFile.getParent().concat("/").concat("libraries"));
            System.out.println(librariesDir);
            if (librariesDir.exists() && librariesDir.isDirectory()) {
                libraries = librariesDir.listFiles();
            } else {
                throw new FileNotFoundException();
            }
        } else {
            throw new FileNotFoundException();
        }
    }

    /**
     * Set tester-server.jar file.
     */
    public void setServerTesterFile(File serverTesterFile) {
        this.serverTesterFile = serverTesterFile;
    }

    /**
     * Get AID of a server by hostname.
     * 
     * @param hostname
     * @return server AID.
     */
    public AID getAIDByHostname(String hostname) {
        return availableServers.get(hostname);
    }

    public DeviceResourcesResponse getDeviceResources(Device device, AID receiver, long timeout) throws Exception {
        return agent.getDeviceResources(device, self, receiver, timeout);
    }

    public DeviceIpSlasResponse getDeviceIpSlas(Device device, AID receiver, long timeout) throws Exception {
        return agent.getDeviceIpSlas(device, self, receiver, timeout);
    }

    public DeviceInterfacesResponse getDeviceInterfaces(Device device, AID receiver, long timeout) throws Exception {
        return agent.getDeviceInterfaces(device, self, receiver, timeout);
    }

    public SqlInsertableRowDescriptorResponse getDeviceResourceStats(Device device, DeviceResource resource,
            AID receiver, long timeout) throws Exception {
        return agent.getDeviceResourceStats(device, resource, self, receiver, timeout);
    }

    public SqlInsertableRowDescriptorResponse getDeviceResourceStats(Device device, Interface iface, AID receiver,
            long timeout) throws Exception {
        return agent.getDeviceInterfaceStats(device, iface, self, receiver, timeout);
    }

    public SqlInsertableRowDescriptorResponse getDeviceResourceStats(Device device, IpSla ipSla, AID receiver,
            long timeout) throws Exception {
        return agent.getDeviceIpSlaStats(device, ipSla, self, receiver, timeout);
    }
    
    public DeviceQousResponce getDeviceQous(Device device,AID receiver , long timeout) throws Exception{
    	return agent.getDeviceQous(device,self,receiver,timeout);
    }
    
    public SqlInsertableRowDescriptorResponse getDeviceQousStast(Device device,QosClass qous,AID receiver,
    		long timeout) throws Exception{
    	return agent.getDeviceQousStast(device, qous, self, receiver, timeout);
    }
    public List<ServerObject> getStartedServers() {
        if (startedServers == null) {
            startedServers = new ArrayList<ServerObject>(this.servers);
            for (ServerObject server : this.servers) {
                if (getAIDByHostname(server.getSshCredential().getHostname()) == null) {
                    startedServers.remove(server);
                }
            }
        }
        return startedServers;
    }

    /**
     * Client running state.
     * 
     * @return client state
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Start all servers in {@link #servers}
     * 
     * @throws EmptyException
     *             if no one server was started.
     */
    private void startServers() throws EmptyException {
        CountDownLatch latch = new CountDownLatch(servers.size());
        XStream xstream = new XStream(new DomDriver());
        String xml = xstream.toXML(self).replaceAll("[\"]+", "\\\\\"");
        for (ServerObject server : servers) {
            new ServerRunner(server, xml, latch).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
        }
        
        if (connectors.size() == 0) {
            throw new EmptyException("No server was started.");
        }
    }

    public void start() throws StartException {
        if (isRunning()) {
            throw new IllegalStateException("client is already running.");
        }
        try {
            validateServerTesterFile();
            startJade();
            startServers();
            CountDownLatch latch = new CountDownLatch(1);
            agent.discovery(connectors.size(), latch);
            try {
                latch.await();
            } catch (InterruptedException e) {
            }
            if (availableServers.size() == 0) {
                throw new EmptyException("No server was started.");
            }
        } catch (Exception e) {
            running = true;
            stop();
            throw new StartException(e.getMessage(), e);
        }
        running = true;
    }

    public void stop() {
        if (isRunning()) {
            manager.stop();
            for (SSHConnector connector : connectors) {
                connector.disconnect();
            }
            running = false;
        }
    }

    private class ServerRunner extends Thread {
        private SSHConnector connector;
        private ServerObject server;
        private String clientAID;
        private CountDownLatch latch;

        public ServerRunner(ServerObject server, String clientAID, CountDownLatch latch) {
            super("SSH-Connector-" + server.getSshCredential().getHostname());
            connector = new SSHConnector();
            this.server = server;
            this.clientAID = clientAID;
            this.latch = latch;
        }

        @Override
        public void run() {
            boolean executing = false;
            SSHCredential ssh = server.getSshCredential();
            connector.setWorkingDirectory(server.getGlassfishPath());
            try {
                connector.connect(ssh.getHostname(), ssh.getUsername(), ssh.getPassword(), ssh.getPort());
                connector.completeWorkingDirectory();
                File[] files = connector.getFiles(libraries);
                connector.uploadFiles(files, "libraries");
                connector.uploadFile(serverTesterFile, "", null);
                String[] args = new String[] { ssh.getHostname(), server.getJadeHost(), "\"" + clientAID + "\"",
                        "\"" + server.getProjects() + "\"" };
                executing = connector.executeJar(serverTesterFile.getName(), args);
                if (executing) {
                    connectors.add(connector);
                }
            } catch (JSchException e) {
            	e.printStackTrace();
            } catch (SftpException e) {
                e.printStackTrace();
            } catch (Exception e) {
                executing = false;
                e.printStackTrace();
            } finally {
                if (!executing) {
                    connector.disconnect();
                }
                latch.countDown();
            }
        }
    }
}
