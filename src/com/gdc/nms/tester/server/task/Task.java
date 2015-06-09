package com.gdc.nms.tester.server.task;

import java.io.IOException;
import java.io.Serializable;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import com.gdc.nms.tester.server.ServerAgent;

public abstract class Task implements Runnable{

	private ServerAgent agent;
	private ACLMessage reply;

	public final void setReplyMessage(ACLMessage reply){
		this.reply = reply;
	}

	public final void setServerAgent(ServerAgent agent){
		this.agent = agent;
	}

	protected final void sendMessage(final Serializable object){
		agent.addBehaviour(new OneShotBehaviour(agent) {
			@Override
			public void action() {
				try {
					reply.setContentObject(object);
					myAgent.send(reply);
				} catch (IOException e) {
					System.out.println("ERROR");
					e.printStackTrace();
				}
			}
		});
	}

}
