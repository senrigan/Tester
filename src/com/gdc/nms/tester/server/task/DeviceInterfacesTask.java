package com.gdc.nms.tester.server.task;

import java.util.List;
import java.util.Set;

import com.gdc.nms.common.Ip;
import com.gdc.nms.model.Device;
import com.gdc.nms.model.DeviceResource;
import com.gdc.nms.model.Interface;
import com.gdc.nms.server.drivers.snmp.Driver;
import com.gdc.nms.server.drivers.snmp.DriverManager;
import com.gdc.nms.tester.common.response.DeviceInterfacesResponse;
import com.gdc.nms.tester.common.response.DeviceResourcesResponse;
import com.gdc.nms.tester.common.response.Response;

public class DeviceInterfacesTask extends Task {

	private Device device;

	public DeviceInterfacesTask(Device device) {
		this.device = device;
	}

	@Override
	public void run() {
		boolean ping = Ip.ping(device.getIp(), 120000, 1000);
		if(ping){
			Driver driver = null;
			try{
				driver = DriverManager.getInstance().getDriver(device);
				Set<Interface> interfaces = driver.getInterfaces();
				Response response = new DeviceInterfacesResponse(interfaces);
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
		}
	}

}
