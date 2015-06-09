package com.gdc.nms.tester.server;

import jade.core.AID;

import java.lang.reflect.Field;
import java.util.List;

import com.gdc.nms.model.Project;
import com.gdc.nms.server.agent.SnmpAgent;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class Main {

    private static void addEntityService() {
        try {
            Field field = SnmpAgent.class.getDeclaredField("entityService");
            field.setAccessible(true);
            field.set(SnmpAgent.getInstance(), MyEntityService.getInstance());
        } catch (Exception e) {
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        if (args.length < 3) {
            System.exit(1);
        }
        // args[0] = address
        // args[1] = mtps_address
        // args[2] = xml_client_AID
        // args[3] = xml_projects
        XStream xstream = new XStream(new DomDriver());
        String address = args[0];
        String mtpAddress = args[1];
        List<Project> projects = null;
        AID clientAID = null;
        try {
            clientAID = (AID) xstream.fromXML(args[2]);
            projects = (List<Project>) xstream.fromXML(args[3]);
            xstream = null;
        } catch (Exception e) {
            System.exit(1);
        }
        Server server = new Server(address, mtpAddress);
        try {
            addEntityService();
            MyEntityService.getInstance().setProjects(projects);
            server.setClientAID(clientAID);
            server.start();
        } catch (Exception e) {
            server.stop();
            System.exit(1);
        }
    }
}
