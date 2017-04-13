package com.almundo.maven.dispatchMessages.process;

import java.util.concurrent.BlockingQueue;

import com.almundo.maven.dispatchMessages.dto.Employee;
import com.almundo.maven.dispatchMessages.dto.Message;
import com.almundo.maven.dispatchMessages.constants.DispatchMessagesConstants;
import com.almundo.maven.dispatchMessages.util.DispatchMessagesUtil;
import com.almundo.maven.dispatchMessages.enums.MessageProcessType;

public class Dispatcher extends Thread{
	
	private Thread thread;
	private Message message;
	private BlockingQueue <Employee> employeesPriorityQueue;
	private int retryNumber = 10;
	
	/**
	 * Constructor get the message and set its possible assignment employees
	 * @param message
	 * @param employeesPriorityQueue
	 */
	public Dispatcher(Message message, BlockingQueue<Employee> employeesPriorityQueue){
		this.message = message;
		this.employeesPriorityQueue = employeesPriorityQueue;
	}
	
	/**
	 * Take the first element of the queue and assign the employee for serve the message
	 * When employee is not busy, put in attention queue again
	 * @throws InterruptedException
	 */
	public void dispatchCall() throws InterruptedException{
		
		if(MessageProcessType.DELAYED.getProcessType() == message.getProcessType().getProcessType()) Thread.yield();
		
		if(employeesPriorityQueue.isEmpty() && message.getAssignedEmployee() == null){
			if(retryNumber > 0) {
				retryAssignment();
			}else{
				System.out.println(message.getMessageDetail()+" can't be served");
			}
		}else if(message.getAssignedEmployee() == null){		
			Employee serverEmployee = this.employeesPriorityQueue.poll();
			message.setAssignedEmployee(serverEmployee);
			System.out.println("serverEmployee for "+message.getMessageDetail()+" "+serverEmployee.getEmployeeName()+", "+serverEmployee.getEmployeeType().getEmployeeType());

			int milliseconds = DispatchMessagesUtil.getSpleepingTime(DispatchMessagesConstants.minLimitToProcess, DispatchMessagesConstants.maxLimitToProcess);
			Thread.sleep(milliseconds);
			
			employeesPriorityQueue.put(serverEmployee);
			
		}
		
		/*System.out.println("*******begin checking employees**************");
		while(!employeesPriorityQueue.isEmpty()){
    		Employee emp = (Employee)employeesPriorityQueue.peek();
    		System.out.println(emp.getEmployeeName() +" "+emp.getEmployeeType().getEmployeeType());
    		employeesPriorityQueue.poll();
        }
		System.out.println("*******end checking employees**************");*/
	}
	
	/**
	 * Retry to serve the message 5 times
	 * @throws InterruptedException
	 */
	public void retryAssignment() throws InterruptedException{
		System.out.println("It will retray to serve "+message.getMessageDetail()+"... retryNumber="+retryNumber);
		while(retryNumber > 0){
			int milliseconds = DispatchMessagesUtil.getSpleepingTime(DispatchMessagesConstants.minLimitToProcess, DispatchMessagesConstants.maxLimitToProcess);
			
			Thread.sleep(milliseconds);
			retryNumber--;
			dispatchCall();
		}
	}
	
	public void run(){
		try{
			dispatchCall();
			System.out.println(message.getMessageDetail() +" was served by "+message.getAssignedEmployee().getEmployeeName()+ " who is "+message.getAssignedEmployee().getEmployeeType().getEmployeeTypeDescription());
		}catch(InterruptedException e){
			System.out.println("Thread "+message.getMessageDetail()+" interrupted.");
		}catch(Exception e){
			System.out.println("Run Exception "+e.getMessage());
		}
	}

	
	public void start(){
		if(thread == null){
			thread = new Thread(this, message.getMessageDetail());
			thread.start();
		}
	}
	
}
