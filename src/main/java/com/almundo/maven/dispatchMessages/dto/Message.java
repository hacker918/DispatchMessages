package com.almundo.maven.dispatchMessages.dto;

import com.almundo.maven.dispatchMessages.enums.MessageProcessType;

public class Message {
	private String messageDetail;
	private Employee assignedEmployee; //the employee that served the message
	private MessageProcessType processType; //can be immediate or delay
	
	public Message(String messageDetail){
		this.messageDetail = messageDetail;
	}

	public String getMessageDetail() {
		return messageDetail;
	}

	public void setMessageDetail(String messageDetail) {
		this.messageDetail = messageDetail;
	}

	public Employee getAssignedEmployee() {
		return assignedEmployee;
	}

	public void setAssignedEmployee(Employee assignedEmployee) {
		this.assignedEmployee = assignedEmployee;
	}

	public MessageProcessType getProcessType() {
		return processType;
	}

	public void setProcessType(MessageProcessType processType) {
		this.processType = processType;
	}
}
