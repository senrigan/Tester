package com.gdc.nms.tester.common.request;

import com.gdc.nms.model.Device;
import com.gdc.nms.model.DeviceResource;
import com.gdc.nms.model.Interface;
import com.gdc.nms.model.IpSla;

public class DeviceIpSlaStatsRequest implements Request{
	private Device device;
	private IpSla ipSla;

	public DeviceIpSlaStatsRequest(Device device, IpSla ipSla) {
		this.device = device;
		this.ipSla = ipSla;
	}

	public Device getDevice() {
		return device;
	}

	public IpSla getIpSla() {
		return ipSla;
	}
}
