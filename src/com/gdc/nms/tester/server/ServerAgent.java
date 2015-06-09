package com.gdc.nms.tester.server;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import com.gdc.nms.model.Device;
import com.gdc.nms.tester.common.AgentsUtil;
import com.gdc.nms.tester.common.request.DeviceInterfaceStatsRequest;
import com.gdc.nms.tester.common.request.DeviceIpSlaStatsRequest;
import com.gdc.nms.tester.common.request.DeviceQousStastRequest;
import com.gdc.nms.tester.common.request.DeviceResourceStatsRequest;
import com.gdc.nms.tester.server.task.DeviceInterfaceStatsTask;
import com.gdc.nms.tester.server.task.DeviceInterfacesTask;
import com.gdc.nms.tester.server.task.DeviceIpSlaStatsTask;
import com.gdc.nms.tester.server.task.DeviceIpSlasTask;
import com.gdc.nms.tester.server.task.DeviceQousStatsTask;
import com.gdc.nms.tester.server.task.DeviceQousTask;
import com.gdc.nms.tester.server.task.DeviceResourceStatsTask;
import com.gdc.nms.tester.server.task.DeviceResourcesTask;
import com.gdc.nms.tester.server.task.Task;

public class ServerAgent extends Agent {

    private ExecutorService threadPool;
    private String mtps;
    private AID clientAID;
    private String serverHostname;

    public ServerAgent(ExecutorService threadPool, String serverHostname, String mtps) {
        this.threadPool = threadPool;
        this.mtps = mtps;
        this.serverHostname = serverHostname;
    }

    public void setClientAID(AID clientAID) {
        this.clientAID = clientAID;
    }

    @Override
    protected void setup() {
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
            	System.out.println(clientAID);
                ACLMessage message = getACLMessage(AgentsUtil.ALIVE_PERFORMATIVE);
                message.setContent(serverHostname);
                send(message);
                System.out.println();
            }
        });
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate pattern = MessageTemplate.MatchPerformative(AgentsUtil.TASK_PERFORMATIVE);
                ACLMessage receive = receive(pattern);
                if (receive == null) {
                    System.out.println("Waiting message");
                    block();
                } else {
                    String task = receive.getUserDefinedParameter(AgentsUtil.TASK_KEY);
                    System.out.println("task: " + task);
                    if (AgentsUtil.DEVICERESOURCES_TASK.equals(task)) {
                        try {
                            Device device = (Device) receive.getContentObject();
                            Task _task = new DeviceResourcesTask(device);
                            _task.setReplyMessage(getReply(receive));
                            _task.setServerAgent(ServerAgent.this);
                            threadPool.execute(_task);
                        } catch (UnreadableException e) {
                            ACLMessage reply = getReply(receive);
                            try {
                                reply.setContentObject(e);
                            } catch (IOException e1) {
                            }
                        }
                    } else if (AgentsUtil.DEVICEIPSLAS_TASK.equals(task)) {
                        try {
                            Device device = (Device) receive.getContentObject();
                            Task _task = new DeviceIpSlasTask(device);
                            _task.setReplyMessage(getReply(receive));
                            _task.setServerAgent(ServerAgent.this);
                            threadPool.execute(_task);
                        } catch (UnreadableException e) {
                            ACLMessage reply = getReply(receive);
                            try {
                                reply.setContentObject(e);
                            } catch (IOException e1) {
                            }
                        }
                    } else if (AgentsUtil.DEVICEINTERFACES_TASK.equals(task)) {
                        try {
                            Device device = (Device) receive.getContentObject();
                            Task _task = new DeviceInterfacesTask(device);
                            _task.setReplyMessage(getReply(receive));
                            _task.setServerAgent(ServerAgent.this);
                            threadPool.execute(_task);
                        } catch (UnreadableException e) {
                            ACLMessage reply = getReply(receive);
                            try {
                                reply.setContentObject(e);
                            } catch (IOException e1) {
                            }
                        }
                    } else if (AgentsUtil.DEVICERESOURCESTATS_TASK.equals(task)) {
                        try {
                            DeviceResourceStatsRequest device = (DeviceResourceStatsRequest) receive.getContentObject();
                            Task _task = new DeviceResourceStatsTask(device);
                            _task.setReplyMessage(getReply(receive));
                            _task.setServerAgent(ServerAgent.this);
                            threadPool.execute(_task);
                        } catch (UnreadableException e) {
                            ACLMessage reply = getReply(receive);
                            try {
                                reply.setContentObject(e);
                            } catch (IOException e1) {
                            }
                        }
                    } else if (AgentsUtil.DEVICEIPSLASTATS_TASK.equals(task)) {
                        try {
                            DeviceIpSlaStatsRequest device = (DeviceIpSlaStatsRequest) receive.getContentObject();
                            Task _task = new DeviceIpSlaStatsTask(device);
                            _task.setReplyMessage(getReply(receive));
                            _task.setServerAgent(ServerAgent.this);
                            threadPool.execute(_task);
                        } catch (UnreadableException e) {
                            ACLMessage reply = getReply(receive);
                            try {
                                reply.setContentObject(e);
                            } catch (IOException e1) {
                            }
                        }
                    } else if (AgentsUtil.DEVICEINTERFACESTATS_TASK.equals(task)) {
                        try {
                            DeviceInterfaceStatsRequest device = (DeviceInterfaceStatsRequest) receive
                                .getContentObject();
                            Task _task = new DeviceInterfaceStatsTask(device);
                            _task.setReplyMessage(getReply(receive));
                            _task.setServerAgent(ServerAgent.this);
                            threadPool.execute(_task);
                        } catch (UnreadableException e) {
                            ACLMessage reply = getReply(receive);
                            try {
                                reply.setContentObject(e);
                            } catch (IOException e1) {
                            }
                        }
                        //TODO crear para obtener qous
                    } else if(AgentsUtil.DEVICEQOUS_TASK.equals(task)){
                    	try{
                    		  Device device = (Device) receive.getContentObject();
                              Task _task = new DeviceQousTask(device);
                              _task.setReplyMessage(getReply(receive));
                              _task.setServerAgent(ServerAgent.this);
                              threadPool.execute(_task);
                    	}catch(UnreadableException e){
                    		  ACLMessage reply = getReply(receive);
                              try {
                                  reply.setContentObject(e);
                              } catch (IOException e1) {
                              }
                    	}
                    	
                    }else if(AgentsUtil.DEVICEQOUSTATS_TASK.equals(task)){
                    	try{
                    		DeviceQousStastRequest device=(DeviceQousStastRequest)receive.getContentObject();
                    		Task _task=new DeviceQousStatsTask(device);
                    		_task.setReplyMessage(getReply(receive));
                    		_task.setServerAgent(ServerAgent.this);
                    		threadPool.execute(_task);
                    	} catch (UnreadableException e) {
                            ACLMessage reply = getReply(receive);
                            try {
                                reply.setContentObject(e);
                            } catch (IOException e1) {
                            }
                        }
                    	
                    }
                    		else {
                        System.out.println("Whats up");
                    }
                }
            }
        });
    }

    private ACLMessage getReply(ACLMessage message) {
        ACLMessage reply = message.createReply();
        reply.setSender(getMyAID());
        return reply;
    }

    private AID getMyAID() {
        AID self = getAID();
        self.addAddresses(AgentsUtil.getMtpAddress(mtps));
        return self;
    }

    private ACLMessage getACLMessage(int perf) {
        ACLMessage message = new ACLMessage(perf);
        message.addReceiver(clientAID);
        message.setSender(getMyAID());
        return message;
    }
}
