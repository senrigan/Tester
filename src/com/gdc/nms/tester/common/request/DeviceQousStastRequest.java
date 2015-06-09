package com.gdc.nms.tester.common.request;

import com.gdc.nms.model.Device;
import com.gdc.nms.model.QosClass;

public class DeviceQousStastRequest implements Request{
	private Device device;
	private QosClass qous;
	
	public DeviceQousStastRequest(Device device , QosClass qous) {
		this.device=device;
		this.qous=qous;
	}

	public Device getDevice() {
		return device;
	}

	public QosClass getQous() {
		return qous;
	}
	
	
}
