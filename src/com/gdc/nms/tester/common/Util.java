package com.gdc.nms.tester.common;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Util {

    /**
     * Verify if a port in a hostname is busy or available.
     * 
     * @param hostname
     * @param port
     * @return
     */
    public static boolean isBusyPort(String hostname, int port) {
        try {
            Socket socket = new Socket(hostname, port);
            socket.close();
            return true;
        } catch (UnknownHostException e) {
        } catch (IOException e) {
        }
        return false;
    }

    /**
     * Avoid Util instances
     */
    private Util() {
    }
    
    public static void main(String[] args) {
			System.out.println(isBusyPort("192.168.207.5", 7778));
	}
}
