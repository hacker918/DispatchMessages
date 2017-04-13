package com.almundo.maven.dispatchMessages.enums;

public enum MessageProcessType {
	IMMEDIATLY(1,"immediatly"),
	DELAYED(2, "delayed");
	
	private int processType;
	private String processTypeDescription;
	
	private MessageProcessType(int processType, String processTypeDescription){
		this.processType = processType;
		this.processTypeDescription = processTypeDescription;
	}
	
	public int getProcessType() {
		return processType;
	}
	public String getProcessTypeDescription() {
		return processTypeDescription;
	}
}
