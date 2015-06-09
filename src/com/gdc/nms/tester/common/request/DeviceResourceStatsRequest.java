package com.gdc.nms.tester.common.request;

import com.gdc.nms.model.Device;
import com.gdc.nms.model.DeviceResource;

public class DeviceResourceStatsRequest implements Request{
	private Device device;
	private DeviceResource deviceResource;

	public DeviceResourceStatsRequest(Device device, DeviceResource deviceResource) {
		this.device = device;
		this.deviceResource = deviceResource;
	}

	public Device getDevice() {
		return device;
	}

	public DeviceResource getDeviceResource() {
		return deviceResource;
	}
}
