package com.gdc.nms.tester.common.response;

import java.util.Map;

import com.gdc.nms.server.eclipselink.SqlInsertableRowDescriptor;

public class SqlInsertableRowDescriptorResponse implements Response {
	private String tableName;
	private Map<String, Object> properties;

	public SqlInsertableRowDescriptorResponse(String tableName, Map<String, Object> properties) {
		this.tableName = tableName;
		this.properties = properties;
	}

	public SqlInsertableRowDescriptor getDescriptor() {
		if(properties == null && tableName == null){
			return null;
		}
		return new SqlInsertableRowDescriptor(tableName, properties);
	}

}
