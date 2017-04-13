package com.almundo.maven.dispatchMessages.enums;

public enum EmployeeType {
	
	OPERATOR(1,"operator"),
	SUPERVISOR(2, "supervisor"),
	DIRECTOR(3, "director");
	
	private int employeeType;
	private String employeeTypeDescription;
	
	private EmployeeType(int employeeType, String employeeTypeDescription){
		this.employeeType = employeeType;
		this.employeeTypeDescription = employeeTypeDescription;
	}
	
	public int getEmployeeType() {
		return employeeType;
	}
	public String getEmployeeTypeDescription() {
		return employeeTypeDescription;
	}
	
}
