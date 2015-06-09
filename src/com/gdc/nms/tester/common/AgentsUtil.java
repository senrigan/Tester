package com.gdc.nms.tester.common;

import jade.lang.acl.ACLMessage;

import java.util.Arrays;
import java.util.List;

public class AgentsUtil {

    /**
     * Message Transport Protocol Address
     */
    private static final String MTP_ADDRESS = "http://%s:7778/acc";

    /**
     * Message Transport Protocol Format
     */
    private static final String MTP_FORMAT = "jade.mtp.http.MessageTransportProtocol(%s)";

    /**
     * Performative used to ALIVE-MESSAGE
     */
    public static final int ALIVE_PERFORMATIVE = ACLMessage.INFORM;

    /**
     * Performative used for a task request
     */
    public static final int TASK_PERFORMATIVE = ACLMessage.REQUEST;

    /**
     * Key used in properties to get current task
     */
    public static final String TASK_KEY = "task";
    //TODO crear la task para qous
    /**
     * DeviceResources Task
     */
    public static final String DEVICERESOURCES_TASK = "task1";

    /**
     * Device IpSlas Task
     */
    public static final String DEVICEIPSLAS_TASK = "task2";

    /**
     * Device Interfaces task
     */
    public static final String DEVICEINTERFACES_TASK = "task3";
    
    /**
     * Device Qous Task
     */
    public static final String DEVICEQOUS_TASK="qousTask";

    /**
     * DeviceResource Stats Task
     */
    public static final String DEVICERESOURCESTATS_TASK = "task4";

    /**
     * Device IpSla Stats Task
     */
    public static final String DEVICEIPSLASTATS_TASK = "task5";

    /**
     * Device Interface Stats Task
     */
    public static final String DEVICEINTERFACESTATS_TASK = "task6";
    /**
     * Device Qous Stats Task
     */
    public static final String DEVICEQOUSTATS_TASK="qoustats";
    /**
     * Client Agent Name
     */
    public static final String CLIENT_AGENT_NAME = "Client";

    /**
     * Client Platform ID
     */
    public static final String CLIENT_PLATFORM_ID = "ClientPlatform";

    /**
     * Server Agent Name
     */
    public static final String SERVER_AGENT_NAME = "Server";

    /**
     * Server Platform ID
     */
    private static final String SERVER_PLATFORM_ID = "%s-Platform";

    /**
     * Get server platform id
     * 
     * @param hostname
     *            prefix used in platform id
     * @return server platform id
     */
    public static String getServerPlaform(String hostname) {
        return String.format(SERVER_PLATFORM_ID, hostname);
    }

    /**
     * Generate Message Transport Protocol Address
     * 
     * @param hostname
     *            Hostname used in mtp address
     * @return mtp address
     */
    public static String getMtpAddress(String hostname) {
        return String.format(MTP_ADDRESS, hostname);
    }

    /**
     * Generate full Message Transport Protocol Format
     * 
     * @param hostname
     *            Hostname used in mtp address
     * @return full mtp
     */
    public static String getMTP(String hostname) {
        return String.format(MTP_FORMAT, getMtpAddress(hostname));
    }

    public static String getMTP(String[] hostnames) {
        return getMTP(Arrays.asList(hostnames));
    }

    public static String getMTP(List<String> hostnames) {
        StringBuffer buf = new StringBuffer();
        for (String hostname : hostnames) {
            buf.append(getMTP(hostname)).append(';');
        }
        buf.setLength(buf.length() - 1);
        return buf.toString();
    }
}
