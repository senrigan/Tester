package com.gdc.nms.tester.server.task;

import java.util.List;

import com.gdc.nms.common.Ip;
import com.gdc.nms.model.Device;
import com.gdc.nms.model.DeviceResource;
import com.gdc.nms.model.IpSla;
import com.gdc.nms.server.drivers.snmp.Driver;
import com.gdc.nms.server.drivers.snmp.DriverManager;
import com.gdc.nms.tester.common.response.DeviceIpSlasResponse;
import com.gdc.nms.tester.common.response.DeviceResourcesResponse;
import com.gdc.nms.tester.common.response.Response;

public class DeviceIpSlasTask extends Task{

	private Device device;

	public DeviceIpSlasTask(Device device) {
		this.device = device;
	}
	
	@Override
	public void run() {
		boolean ping = Ip.ping(device.getIp(), 120000, 1000);
		if(ping){
			Driver driver = null;
			try{
				driver = DriverManager.getInstance().getDriver(device);
				List<IpSla> ipSlas = driver.getIpSlas();
				Response response = new DeviceIpSlasResponse(ipSlas);
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
