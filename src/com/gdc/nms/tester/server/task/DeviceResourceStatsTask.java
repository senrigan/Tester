package com.gdc.nms.tester.server.task;

import java.util.List;

import com.gdc.nms.common.Ip;
import com.gdc.nms.model.DeviceResource;
import com.gdc.nms.server.drivers.snmp.Driver;
import com.gdc.nms.server.drivers.snmp.DriverManager;
import com.gdc.nms.server.eclipselink.SqlInsertableRowDescriptor;
import com.gdc.nms.tester.common.request.DeviceResourceStatsRequest;
import com.gdc.nms.tester.common.response.Response;
import com.gdc.nms.tester.common.response.SqlInsertableRowDescriptorResponse;

public class DeviceResourceStatsTask extends Task {

	private DeviceResourceStatsRequest request;

	public DeviceResourceStatsTask(DeviceResourceStatsRequest request) {
		this.request = request;
	}
	
	@Override
	public void run() {
		boolean ping = Ip.ping(request.getDevice().getIp(), 120000, 1000);
		if(ping){
			Driver driver = null;
			try{
				driver = DriverManager.getInstance().getDriver(request.getDevice());
				SqlInsertableRowDescriptor stats = driver.getStats(request.getDeviceResource(), System.currentTimeMillis(), "Mock");
				Response response;
				if(stats == null){
					response = new SqlInsertableRowDescriptorResponse(null, null);
				}else {
					response = new SqlInsertableRowDescriptorResponse(stats.getTableName(), stats.getProperties());
				}
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
