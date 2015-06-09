package com.gdc.nms.tester.client;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.concurrent.TimeUnit;

/**
 * MessageReceiverBehaviour.java
 * 
 * Behaviour to wait a timeout, limit messages or both, adding a method to
 * handle every message.
 * 
 * @author Samuel Macias <samuel.macias@gdc-cala.com.mx>
 *
 */
public abstract class MessageReceiverBehaviour extends SimpleBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = 6772108226187940894L;

    /**
     * Message Template to wait message
     */
    private MessageTemplate messageTemplate;

    /**
     * Limit number of messages
     */
    private int messageLimit;

    /**
     * Limit of time to wait for messages
     */
    private long timeOut;

    /**
     * Time when behaviour will stop by timeout.
     */
    private long end;

    public MessageReceiverBehaviour(Agent agent, MessageTemplate messageTemplate, long duration, TimeUnit unit) {
        this(agent, messageTemplate, 0, duration, unit);
    }

    public MessageReceiverBehaviour(Agent agent, MessageTemplate messageTemplate, int messageLimit) {
        this(agent, messageTemplate, messageLimit, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * Constructor
     * 
     * @param agent
     *            Agent who will receive messages.
     * @param messageTemplate
     *            Message template used to receive messages.
     * @param messageLimit
     *            Limit of messages, if 0 or less unlimited.
     * @param duration
     *            Limit of time to wait for messages, if 0 or less unlimited.
     * @param unit
     *            Time unit used to convert duration to milliseconds.
     */
    public MessageReceiverBehaviour(Agent agent, MessageTemplate messageTemplate, int messageLimit, long duration,
            TimeUnit unit) {
        super(agent);
        if (messageTemplate == null || unit == null) {
            throw new NullPointerException();
        }
        this.messageTemplate = messageTemplate;
        this.messageLimit = (messageLimit <= 0) ? 0 : messageLimit + 1;
        this.timeOut = unit.toMillis(duration);
    }

    /**
     * Is exceeded the messages limit.
     * 
     * @return messages limit exceeded status.
     */
    private boolean messagesLimitExceeded() {
        if (messageLimit <= 0) {
            return false;
        }

        return messageLimit == 1;
    }

    private boolean timeoutLimitExceeded() {
        if (timeOut <= 0) {
            return false;
        }

        return end < System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        end = System.currentTimeMillis() + timeOut;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void action() {
        ACLMessage receive = myAgent.receive(messageTemplate);
        if (receive == null) {
            block(1000);
        } else {
            messageLimit--;
            handleMessage(receive);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean done() {
        return messagesLimitExceeded() || timeoutLimitExceeded();
    }

    /**
     * Message handle.
     * 
     * @param message
     *            Received message.
     */
    public abstract void handleMessage(ACLMessage message);

}
