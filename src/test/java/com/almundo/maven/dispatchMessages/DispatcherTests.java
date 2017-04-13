package com.almundo.maven.dispatchMessages;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.junit.Test;
import junit.framework.TestCase;

import com.almundo.maven.dispatchMessages.constants.DispatchMessagesConstants;
import com.almundo.maven.dispatchMessages.dto.Employee;
import com.almundo.maven.dispatchMessages.dto.Message;
import com.almundo.maven.dispatchMessages.enums.EmployeeType;
import com.almundo.maven.dispatchMessages.process.Dispatcher;


public class DispatcherTests extends TestCase {
	
	@Test
	public void testTenMessagesWithTenEmployeesOk() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException{
		App app = new App();
		//ordered queue by employee position
		BlockingQueue <Employee> queue = new PriorityBlockingQueue <Employee> (DispatchMessagesConstants.employeesMaxQuantity,App.employeeComparator);
		Method setEmployeesInAQueue = App.class.getDeclaredMethod("setEmployeesInAQueue", BlockingQueue.class);
		setEmployeesInAQueue.setAccessible(true);
		setEmployeesInAQueue.invoke(app, queue);
		
		//quantity of messages to process
		int messageQuantity = 10;
		
    	//generate messages
    	List <Message> messages = new ArrayList<Message>();
    	Method setMessagesToProcess = App.class.getDeclaredMethod("setMessagesToProcess", List.class, int.class);
    	setMessagesToProcess.setAccessible(true);
    	setMessagesToProcess.invoke(app, messages, messageQuantity);
    	
    	for(Message msg : messages){
	    	Dispatcher dispatcher = new Dispatcher(msg,queue);
	    	dispatcher.start();
    	}
    	
    	try {
    		//set maximum time of waiting for serve messages
    		Thread.sleep(10000*messageQuantity);
    	} catch (InterruptedException e) {
    		
		}
    	
    	for(Message msg : messages){
    		assertNotNull(msg.getAssignedEmployee());
    	}
    	
    	//As a minimum, the first three employees should have been serviced by an operator
    	assertEquals(1,messages.get(0).getAssignedEmployee().getEmployeeType().getEmployeeType());
    	assertEquals(1,messages.get(1).getAssignedEmployee().getEmployeeType().getEmployeeType());
    	assertEquals(1,messages.get(2).getAssignedEmployee().getEmployeeType().getEmployeeType());

	}
	
	@Test
	public void testTenMessagesWithFourEmployeesRetrying() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException{
		App app = new App();
		//ordered queue by employee position
		BlockingQueue <Employee> queue = new PriorityBlockingQueue <Employee> (4,App.employeeComparator);
		setEmployeesInAQueue(queue);
		
		//quantity of messages to process
		int messageQuantity = 10;
		
    	//generate messages
    	List <Message> messages = new ArrayList<Message>();
    	Method setMessagesToProcess = App.class.getDeclaredMethod("setMessagesToProcess", List.class, int.class);
    	setMessagesToProcess.setAccessible(true);
    	setMessagesToProcess.invoke(app, messages, messageQuantity);
    	
    	for(Message msg : messages){
	    	Dispatcher dispatcher = new Dispatcher(msg,queue);
	    	dispatcher.start();
    	}
    	
    	try {
    		//set maximum time of waiting for serve messages
    		Thread.sleep(10000*messageQuantity);
    	} catch (InterruptedException e) {
    		
		}
    	
    	for(Message msg : messages){
    		assertNotNull(msg.getAssignedEmployee());
    	}
    	
    	//As a minimum, the first two employees should have been serviced by an operator
    	assertEquals(1,messages.get(0).getAssignedEmployee().getEmployeeType().getEmployeeType());
    	assertEquals(1,messages.get(1).getAssignedEmployee().getEmployeeType().getEmployeeType());

	}
	
	private static void setEmployeesInAQueue(BlockingQueue <Employee> queue) throws InterruptedException{
    	//add employees
    	Employee emp1 = new Employee("Clara",EmployeeType.OPERATOR);
    	Employee emp2 = new Employee("Mario",EmployeeType.OPERATOR);
    	Employee emp3 = new Employee("Victor",EmployeeType.SUPERVISOR);
    	Employee emp4 = new Employee("Analía",EmployeeType.DIRECTOR);
    	
    	
    	//input employees in a queue priority
    	queue.put(emp4);
    	queue.put(emp3);
    	queue.put(emp1);
    	queue.put(emp2);
    
    }
	
}
