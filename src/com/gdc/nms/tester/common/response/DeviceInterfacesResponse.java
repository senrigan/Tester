package com.gdc.nms.tester.common.response;

import java.util.Set;

import com.gdc.nms.model.Interface;

public class DeviceInterfacesResponse implements Response {
	private Set<Interface> interfaces;

	public DeviceInterfacesResponse(Set<Interface> interfaces) {
		this.interfaces = interfaces;
	}

	public Set<Interface> getInterfaces(){
		return interfaces;
	}
}
