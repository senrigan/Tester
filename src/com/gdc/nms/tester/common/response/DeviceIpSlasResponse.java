package com.gdc.nms.tester.common.response;

import java.util.List;

import com.gdc.nms.model.IpSla;

public class DeviceIpSlasResponse implements Response {
	private List<IpSla> ipSlas;

	public DeviceIpSlasResponse(List<IpSla> ipSlas) {
		this.ipSlas = ipSlas;
	}

	public List<IpSla> getIpSlas(){
		return ipSlas;
	}
}
