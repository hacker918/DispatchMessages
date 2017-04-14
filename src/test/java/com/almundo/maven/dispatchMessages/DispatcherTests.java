package com.almundo.maven.dispatchMessages;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.junit.Test;

import junit.framework.TestCase;

import com.almundo.maven.dispatchMessages.constants.DispatchMessagesConstants;
import com.almundo.maven.dispatchMessages.dto.Employee;
import com.almundo.maven.dispatchMessages.dto.Message;
import com.almundo.maven.dispatchMessages.enums.EmployeeType;
import com.almundo.maven.dispatchMessages.enums.MessageProcessType;
import com.almundo.maven.dispatchMessages.process.Dispatcher;


public class DispatcherTests extends TestCase {
	App app;
	BlockingQueue <Employee> queue;
	List<Message>messages;
	
	public void setUp(  ) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		app = new App();
		//ordered queue by employee position
		queue = new PriorityBlockingQueue <Employee> (DispatchMessagesConstants.employeesMaxQuantity,App.employeeComparator);
		Method setEmployeesInAQueue = App.class.getDeclaredMethod("setEmployeesInAQueue", BlockingQueue.class);
		setEmployeesInAQueue.setAccessible(true);
		setEmployeesInAQueue.invoke(app, queue);
		
		messages = new ArrayList<Message>();
    }

    public void tearDown(  ) {
        this.app=null;
        this.queue=null;
        this.messages=null;
    }
	
	/**
	 * Test the priority of employees queue: first operators, second supervisors and third directors.
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testPriorityEmployees() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		System.out.println("****************testPriorityEmployees*******************");
		/*checking the order...*/
		int beforePriority = 0;
    	while(!queue.isEmpty()){
    		Employee emp = (Employee)queue.poll();
    		if(beforePriority > emp.getEmployeeType().getEmployeeType()){
    			fail("Bad priority");
    		}
    		beforePriority = emp.getEmployeeType().getEmployeeType();
    		
        }
		
	}
	
	/**
	 * Test maximum quantity of messages (10) with same quantity of employees to serve them.
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 * @throws NoSuchFieldException 
	 */
	@Test
	public void testTenMessagesWithTenEmployeesOk() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException{
		System.out.println("****************testTenMessagesWithTenEmployeesOk*******************");
		try{
		
			//quantity of messages to process
			int messageQuantity = 10;
	    	//generate messages
	    	setMessages(messageQuantity);
	    	
	    	//before run threads, any message should have assigned employee
	    	for(Message msg : messages){
	    		assertNull(msg.getAssignedEmployee());
	    	}
	    	
	    	//run threads and save them in a syncronizedMap
	    	Map <Integer, Dispatcher> syncronizedMap = Collections.synchronizedMap(new HashMap<Integer, Dispatcher>());
	    	for(int i=0; i<messages.size(); i++){
	    		Message msg = messages.get(i);
		    	Dispatcher dispatcher = new Dispatcher(msg,queue);
		    	syncronizedMap.put(i, dispatcher);
		    	dispatcher.start();
		    	
	    	}
	    	
	    	//set maximum time of waiting for serve a messages
    		Thread.sleep(10000);
    	
	    	//after run threads, all of them should have an assigned employee
	    	for(int i=0; i<syncronizedMap.size(); i++){
	    		Dispatcher dispatcher = syncronizedMap.get(i);
	    		
	    		Field messageField = Dispatcher.class.getDeclaredField("message");
	        	messageField.setAccessible(true);
	        	Message threadMsg = (Message)messageField.get(dispatcher);
	        	
	        	assertNotNull(threadMsg.getAssignedEmployee());
	        
	    	}
	    	
	    	//As a minimum, the first message should have been serviced by an operator
	    	assertEquals(1,messages.get(0).getAssignedEmployee().getEmployeeType().getEmployeeType());
	    	
		}catch(Exception e){
    		System.out.println(e.getMessage());
    		fail("Exception checking messages status");
    	}

	}
	
	/**
	 * set messages to be processed
	 * @param messages
	 * @param messageQuantity
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private void setMessages(int messageQuantity) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Method setMessagesToProcess = App.class.getDeclaredMethod("setMessagesToProcess", List.class, int.class);
    	setMessagesToProcess.setAccessible(true);
    	setMessagesToProcess.invoke(app, messages, messageQuantity);
	}
	
	/**
	 * Test maximum quantity of messages (10) with less quantity of employees to serve them.
	 * In this case the program make retries when the attention queue is empty
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	@Test
	public void testTenMessagesWithFourEmployeesRetrying() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException{
		System.out.println("****************testTenMessagesWithFourEmployeesRetrying*******************");
		try{
			//there are only 4 employees
			for(Employee emp:queue){
				if(emp.getEmployeeType().getEmployeeType() == 3){
					queue.remove(emp);
				}
			}
			
			//quantity of messages to process
			int messageQuantity = 10;	
	    	//generate messages
	    	setMessages(messageQuantity);
	    	

	    	//before run threads, any message should have assigned employee
	    	for(Message msg : messages){
	    		assertNull(msg.getAssignedEmployee());
	    	}
	    	
	    	//run threads and save them in a syncronizedMap
	    	Map <Integer, Dispatcher> syncronizedMap = Collections.synchronizedMap(new HashMap<Integer, Dispatcher>());
	    	for(int i=0; i<messages.size(); i++){
	    		Message msg = messages.get(i);
		    	Dispatcher dispatcher = new Dispatcher(msg,queue);
		    	syncronizedMap.put(i, dispatcher);
		    	dispatcher.start();
		    	
	    	}
	    	
	    	//set maximum time of waiting for serve messages
    		Thread.sleep(10000*(messageQuantity-4));
		
	    	//after run threads, all of them should have an assigned employee
	    	for(int i=0; i<syncronizedMap.size(); i++){
	    		Dispatcher dispatcher = syncronizedMap.get(i);
	    		
	    		Field messageField = Dispatcher.class.getDeclaredField("message");
	        	messageField.setAccessible(true);
	        	Message threadMsg = (Message)messageField.get(dispatcher);
	        	
	        	assertNotNull(threadMsg.getAssignedEmployee());
	        	
	        	//System.out.println(threadMsg.getMessageDetail()+" "+threadMsg.getAssignedEmployee().getEmployeeType().getEmployeeType()+" "+threadMsg.getAssignedEmployee().getEmployeeName());
	    	}
	    	
	    	//As a minimum, the first message should have been serviced by an operator
	    	assertEquals(1,messages.get(0).getAssignedEmployee().getEmployeeType().getEmployeeType());
	    	
    	} catch (InterruptedException e) {
    		System.out.println("Interrupted thread: "+e.getMessage());
		} catch (Exception e){
			System.out.println(e.getMessage());
    		fail("Exception checking messages status");
		}
	}
	
	/**
	 * This method fill a queue with 4 employees
	 * @param queue
	 * @throws InterruptedException
	 */
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
	
	/**
	 * Test that first messages have immediate process type and rest have delayed process type.
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSetDelayedToRestMessagesToProcess() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		System.out.println("****************testSetDelayedToRestMessagesToProcess*******************");
		//quantity of messages to process
		int messageQuantity = 20;		
    	//generate messages
    	setMessages(messageQuantity);
    	
    	for(int i=0; i<messages.size(); i++){
    		Message msg = messages.get(i);
    		if(i<DispatchMessagesConstants.maxQuantityMessagesToProcess 
    			&& MessageProcessType.IMMEDIATLY.getProcessType() != msg.getProcessType().getProcessType()){
    			fail("First messages should have immediatly process type.");
    		}else if(i>=DispatchMessagesConstants.maxQuantityMessagesToProcess 
    				&& MessageProcessType.DELAYED.getProcessType() != msg.getProcessType().getProcessType()){
    			fail("Rest messages should have delayed process type.");
    		}
    	}
	}
	
	/**
	 * Test more than maximum messages (20) served by common quantity of employees (10).
	 * In this case is useful put an identifier to the rest of messages (DELAYED),
	 * The priority is lower each 10 messages,
	 * Messages with identifier (DELAYED) execute method yield.
	 * The idea is first messages be served first and other messages wait 
	 * and they be put to ready to execute status again.
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	@Test
	public void testTwentyMessagesWithLowPriorityAndYield() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException{
		System.out.println("****************testTwentyMessagesWithLowPriorityAndYield*******************");
		try{
			//quantity of messages to process
			int messageQuantity = 20;	
	    	//generate messages
	    	setMessages(messageQuantity);
	    	
	    	//before run threads, any message should have assigned employee
	    	for(Message msg : messages){
	    		assertNull(msg.getAssignedEmployee());
	    	}
	    	
	    	//run threads and save them in a syncronizedMap
	    	Map <Integer, Dispatcher> syncronizedMap = Collections.synchronizedMap(new HashMap<Integer, Dispatcher>());
	    	for(int i=0; i<messages.size(); i++){
	    		Message msg = messages.get(i);
		    	Dispatcher dispatcher = new Dispatcher(msg,queue);
		    	syncronizedMap.put(i, dispatcher);
		    	dispatcher.start();
		    	
	    	}
	    	
	    	//set maximum time of waiting for serve messages
    		Thread.sleep(10000*(messageQuantity-DispatchMessagesConstants.maxQuantityMessagesToProcess));
    	
	    	//after run threads, all of them should have an assigned employee
	    	for(int i=0; i<syncronizedMap.size(); i++){
	    		Dispatcher dispatcher = syncronizedMap.get(i);
	    		
	    		Field messageField = Dispatcher.class.getDeclaredField("message");
	        	messageField.setAccessible(true);
	        	Message threadMsg = (Message)messageField.get(dispatcher);
	        	
	        	assertNotNull(threadMsg.getAssignedEmployee());
	        
	    	}
	    	
	    	//As a minimum, the first message should have been serviced by an operator
	    	assertEquals(1,messages.get(0).getAssignedEmployee().getEmployeeType().getEmployeeType());
	    	
    	
		}catch(Exception e){
			System.out.println(e.getMessage());
    		fail("Exception checking messages status");
		}

	}
	
}
