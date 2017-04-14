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
	 * Constructor set the message and its possible assignment employees
	 * @param message
	 * @param employeesPriorityQueue
	 */
	public Dispatcher(Message message, BlockingQueue<Employee> employeesPriorityQueue){
		this.message = message;
		this.employeesPriorityQueue = employeesPriorityQueue;
	}
	
	public void start(){
		if(thread == null){
			thread = new Thread(this, message.getMessageDetail());
			thread.start();
		}
	}
	
	public void run(){
		try{
			dispatchCall();
		}catch(InterruptedException e){
			System.out.println("Thread "+message.getMessageDetail()+" interrupted.");
			return;
		}catch(Exception e){
			System.out.println("Run Exception in "+message.getMessageDetail()+": "+e.getStackTrace());
			if(thread.isAlive()){
				try {
					retryAssignment();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return;
				}
			}
		}
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
			//take the first employee with minimum charge and assign in order to serve the call
			Employee serverEmployee = this.employeesPriorityQueue.poll();
			message.setAssignedEmployee(serverEmployee);
			//System.out.println("serverEmployee for "+message.getMessageDetail()+" "+serverEmployee.getEmployeeName()+", "+serverEmployee.getEmployeeType().getEmployeeType());

			//the call could take 5 to 10 seconds
			int milliseconds = DispatchMessagesUtil.getSpleepingTime(DispatchMessagesConstants.minLimitToProcess, DispatchMessagesConstants.maxLimitToProcess);
			Thread.sleep(milliseconds);
			
			//when the employee finish to serve the call, he return to the available employees in queue
			employeesPriorityQueue.put(serverEmployee);
			System.out.println(message.getMessageDetail() +" was served by "+message.getAssignedEmployee().getEmployeeName()+ " who is "+message.getAssignedEmployee().getEmployeeType().getEmployeeTypeDescription());
		}
	}
	
	/**
	 * Retry to serve the message retryNumber times
	 * @throws InterruptedException
	 */
	public void retryAssignment() throws InterruptedException{
		//System.out.println("It will retray to serve "+message.getMessageDetail()+"... retryNumber="+retryNumber);
		while(retryNumber > 0){
			int milliseconds = DispatchMessagesUtil.getSpleepingTime(DispatchMessagesConstants.minLimitToProcess, DispatchMessagesConstants.maxLimitToProcess);
			
			Thread.sleep(milliseconds);
			retryNumber--;
			dispatchCall();
		}
	}

}
