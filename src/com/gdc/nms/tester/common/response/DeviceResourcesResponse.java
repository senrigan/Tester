package com.gdc.nms.tester.common.response;

import java.util.List;

import com.gdc.nms.model.DeviceResource;

public class DeviceResourcesResponse implements Response {
	private List<DeviceResource> deviceResources;
	
	public DeviceResourcesResponse(List<DeviceResource> deviceResources) {
		this.deviceResources = deviceResources;
	}

	public List<DeviceResource> getDeviceResources(){
		return this.deviceResources;
	}
}
