package com.almundo.maven.dispatchMessages.dto;

import com.almundo.maven.dispatchMessages.enums.EmployeeType;

public class Employee {
	
	private String employeeName;
	private EmployeeType employeeType;
	
	public Employee(String employeeName, EmployeeType employeeType){
		this.employeeName = employeeName;
		this.employeeType = employeeType;
	}
	
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public EmployeeType getEmployeeType() {
		return employeeType;
	}
	public void setEmployeeType(EmployeeType employeeType) {
		this.employeeType = employeeType;
	}

}
