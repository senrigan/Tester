package com.gdc.nms.tester.common.response;

import java.util.List;

import com.gdc.nms.model.QosClass;

public class DeviceQousResponce implements Response{
	private List<QosClass> qous;

	public  DeviceQousResponce(List<QosClass> qous) {
		this.qous=qous;
	}

	public List<QosClass> getQos(){
		return qous;
	}
}
