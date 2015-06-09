package com.gdc.nms.tester.server.task;

import java.util.ArrayList;
import java.util.List;

import com.gdc.nms.common.Ip;
import com.gdc.nms.model.Device;
import com.gdc.nms.model.QosClass;
import com.gdc.nms.server.drivers.snmp.Driver;
import com.gdc.nms.server.drivers.snmp.DriverManager;
import com.gdc.nms.tester.common.exception.UnreachableException;
import com.gdc.nms.tester.common.response.DeviceQousResponce;
import com.gdc.nms.tester.common.response.Response;

public class DeviceQousTask extends Task {
	private Device device;
	public DeviceQousTask(Device device){
		this.device=device;
	}

	@Override
	public void run() {
		boolean ping = Ip.ping(device.getIp(), 120000, 1000);
		if(ping){
			Driver driver = null;
			try{
				driver = DriverManager.getInstance().getDriver(device);
			
				List<Integer> listifIndexes=new ArrayList<Integer>();
				listifIndexes.addAll(driver.getIfIndexesWithQos());
				List<QosClass> deviceQous = driver.getQosClasses(listifIndexes);
				
				Response response = new DeviceQousResponce(deviceQous);
				sendMessage(response);
			} catch (Exception e){
				sendMessage(e);
			} finally {
				if(driver != null){
					driver.unbind();
				}
			}
		} else {
			//	TODO unreachable
			sendMessage(new UnreachableException(" can not reach the device "));
		}
		
	}
	
	
}
