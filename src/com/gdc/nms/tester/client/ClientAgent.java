package com.gdc.nms.tester.client;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.AMSService;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.gdc.nms.model.Device;
import com.gdc.nms.model.DeviceResource;
import com.gdc.nms.model.Interface;
import com.gdc.nms.model.IpSla;
import com.gdc.nms.model.QosClass;
import com.gdc.nms.tester.common.AgentsUtil;
import com.gdc.nms.tester.common.exception.EmptyException;
import com.gdc.nms.tester.common.exception.TimeoutException;
import com.gdc.nms.tester.common.request.DeviceInterfaceStatsRequest;
import com.gdc.nms.tester.common.request.DeviceIpSlaStatsRequest;
import com.gdc.nms.tester.common.request.DeviceQousStastRequest;
import com.gdc.nms.tester.common.request.DeviceResourceStatsRequest;
import com.gdc.nms.tester.common.response.DeviceInterfacesResponse;
import com.gdc.nms.tester.common.response.DeviceIpSlasResponse;
import com.gdc.nms.tester.common.response.DeviceQousResponce;
import com.gdc.nms.tester.common.response.DeviceResourcesResponse;
import com.gdc.nms.tester.common.response.Response;
import com.gdc.nms.tester.common.response.SqlInsertableRowDescriptorResponse;

public class ClientAgent extends Agent {

    /**
     * 
     */
    private static final long serialVersionUID = 4081621426393091387L;
    /**
     * Servers discovered in {@link #discovery(int)}
     */
    private Map<String, AID> servers;

    public ClientAgent(Map<String, AID> servers) {
        this.servers = servers;
    }

    public void discovery(final int messageLimit, final CountDownLatch latch) throws EmptyException {
        addBehaviour(new MessageReceiverBehaviour(this,
            MessageTemplate.MatchPerformative(AgentsUtil.ALIVE_PERFORMATIVE), messageLimit, 30, TimeUnit.SECONDS) {
            private static final long serialVersionUID = 8236561743086314413L;

            @Override
            public void handleMessage(ACLMessage message) {
                AID sender = message.getSender();
                String hostname = message.getContent();
                servers.put(hostname, sender);
                System.out.println("message");
            }

            @Override
            public int onEnd() {
                latch.countDown();
                return 0;
            }
        });
    }

    public DeviceResourcesResponse getDeviceResources(Device device, AID sender, AID receiver, long timeout)
            throws Exception {
        return sendTask(DeviceResourcesResponse.class, AgentsUtil.DEVICERESOURCES_TASK, sender, receiver, device,
            timeout);
    }

    public DeviceIpSlasResponse getDeviceIpSlas(Device device, AID sender, AID receiver, long timeout) throws Exception {
        return sendTask(DeviceIpSlasResponse.class, AgentsUtil.DEVICEIPSLAS_TASK, sender, receiver, device, timeout);
    }

    public DeviceInterfacesResponse getDeviceInterfaces(Device device, AID sender, AID receiver, long timeout)
            throws Exception {
        return sendTask(DeviceInterfacesResponse.class, AgentsUtil.DEVICEINTERFACES_TASK, sender, receiver, device,
            timeout);
    }
    
    public DeviceQousResponce getDeviceQous(Device device, AID sender, AID receiver,long timeout) throws Exception {
		
		return sendTask(DeviceQousResponce.class,AgentsUtil.DEVICEQOUS_TASK,sender,receiver,device,timeout);
	}

    
    public SqlInsertableRowDescriptorResponse getDeviceQousStast(Device device,
    			 QosClass qous,AID sender,AID receiver,long timeout) throws Exception{
    				DeviceQousStastRequest request=new DeviceQousStastRequest(device,qous);
    	return sendTask(SqlInsertableRowDescriptorResponse.class,AgentsUtil.DEVICEQOUSTATS_TASK,sender,
    			receiver,request,timeout);
    }
    
    public SqlInsertableRowDescriptorResponse getDeviceResourceStats(Device device, DeviceResource resource,
            AID sender, AID receiver, long timeout) throws Exception {
        DeviceResourceStatsRequest request = new DeviceResourceStatsRequest(device, resource);
        return sendTask(SqlInsertableRowDescriptorResponse.class, AgentsUtil.DEVICERESOURCESTATS_TASK, sender,
            receiver, request, timeout);
    }

    public SqlInsertableRowDescriptorResponse getDeviceInterfaceStats(Device device, Interface iface, AID sender,
            AID receiver, long timeout) throws Exception {
        DeviceInterfaceStatsRequest request = new DeviceInterfaceStatsRequest(device, iface);
        return sendTask(SqlInsertableRowDescriptorResponse.class, AgentsUtil.DEVICEINTERFACESTATS_TASK, sender,
            receiver, request, timeout);
    }

    public SqlInsertableRowDescriptorResponse getDeviceIpSlaStats(Device device, IpSla ipSla, AID sender, AID receiver,
            long timeout) throws Exception {
        DeviceIpSlaStatsRequest request = new DeviceIpSlaStatsRequest(device, ipSla);
        return sendTask(SqlInsertableRowDescriptorResponse.class, AgentsUtil.DEVICEIPSLASTATS_TASK, sender,
            receiver, request, timeout);
    }
    
   

    @SuppressWarnings("unchecked")
    private <T extends Response> T sendTask(Class<T> clazz, String task, AID sender, AID receiver,
            Serializable objectSend, long timeout) throws TimeoutException, FailureException, UnreadableException,
            Exception {
        ACLMessage message = new ACLMessage(AgentsUtil.TASK_PERFORMATIVE);
        String conversation = task.concat("-").concat(Long.toString(System.currentTimeMillis()));
        message.addUserDefinedParameter(AgentsUtil.TASK_KEY, task);
        message.setConversationId(conversation);
        message.addReceiver(receiver);
        message.setSender(sender);
        message.setContentObject(objectSend);
        send(message);
        MessageTemplate pattern = MessageTemplate.MatchConversationId(conversation);
        ACLMessage receive = blockingReceive(pattern, timeout);
        if (receive == null) {
            throw new TimeoutException("TIMEOUT");
        } else if (receive.getPerformative() == ACLMessage.FAILURE) {
            throw new FailureException(receive.getContent());
        } else {
            try {
                Serializable object = receive.getContentObject();
                if (object instanceof Exception) {
                    throw (Exception) object;
                } else {
                    try {
                        return clazz.cast( object);
                    } catch (Exception e) {
                        throw e;
                    }
                }
            } catch (UnreadableException e) {
                throw e;
            }
        }
    }

	
}
