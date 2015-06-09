package com.gdc.nms.tester.common.request;

import com.gdc.nms.model.Device;
import com.gdc.nms.model.DeviceResource;
import com.gdc.nms.model.Interface;

public class DeviceInterfaceStatsRequest implements Request{
	private Device device;
	private Interface iface;

	public DeviceInterfaceStatsRequest(Device device, Interface iface) {
		this.device = device;
		this.iface = iface;
	}

	public Device getDevice() {
		return device;
	}

	public Interface getInterface() {
		return iface;
	}
}
