package com.almundo.maven.dispatchMessages;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import com.almundo.maven.dispatchMessages.constants.DispatchMessagesConstants;
import com.almundo.maven.dispatchMessages.dto.*;
import com.almundo.maven.dispatchMessages.enums.EmployeeType;
import com.almundo.maven.dispatchMessages.process.Dispatcher;
import com.almundo.maven.dispatchMessages.enums.MessageProcessType;;


public class App 
{
	/**
	 * Initialize a queue with employees, a messages list and dispatch the messages
	 * first messages are served by operators, else supervisors, else directors
	 * @param args
	 */
    public static void main( String[] args )
    {
    	int messageQuantity = 0;
    	
    	try{
    		
    		//ordered queue by employee position
    		BlockingQueue <Employee> queue = new PriorityBlockingQueue <Employee> (DispatchMessagesConstants.employeesMaxQuantity,employeeComparator);
    		setEmployeesInAQueue(queue);
    		
    		//input quantity of messages to process
    		messageQuantity = getMessagesQuantity();
    		
	    	//generate messages
	    	List <Message> messages = new ArrayList<Message>();
	    	setMessagesToProcess(messages,messageQuantity);
	    	
	    	//process messages
	    	int priority = Thread.MAX_PRIORITY;
	    	for(int i=0; i<messages.size();i++){
	    		Message msg = messages.get(i);
		    	Dispatcher dispatcher = new Dispatcher(msg,queue);
		    	
		    	//set less priority each maxQuantityMessagesToProcess threads
	    		dispatcher.setPriority(priority);	    		
	    		if((i+1) % DispatchMessagesConstants.maxQuantityMessagesToProcess == 0){
	    			if(priority > Thread.MIN_PRIORITY) priority--;
	    		}
		    	
		    	dispatcher.start();
	    	}
	    	
    	}catch(Exception e){
    		System.out.println("Exception: "+e.getMessage());
    	}
    	
    }
    
    /**
     * Fill a queue with employees and their positions
     * @param queue
     * @throws InterruptedException
     */
    private static void setEmployeesInAQueue(BlockingQueue <Employee> queue) throws InterruptedException{
    	//add employees
    	Employee emp1 = new Employee("Clara",EmployeeType.OPERATOR);
    	Employee emp2 = new Employee("Mario",EmployeeType.OPERATOR);
    	Employee emp3 = new Employee("Mara",EmployeeType.OPERATOR);
    	Employee emp4 = new Employee("Victor",EmployeeType.SUPERVISOR);
    	Employee emp5 = new Employee("Victoria",EmployeeType.SUPERVISOR);
    	Employee emp6 = new Employee("Micaela",EmployeeType.SUPERVISOR);
    	Employee emp7 = new Employee("Analía",EmployeeType.DIRECTOR);
    	Employee emp8 = new Employee("Diego",EmployeeType.DIRECTOR);
    	Employee emp9 = new Employee("Pedro",EmployeeType.DIRECTOR);
    	Employee emp10 = new Employee("Agustín",EmployeeType.DIRECTOR);
    	
    	//input employees in a queue priority
    	queue.put(emp7);
    	queue.put(emp8);
    	queue.put(emp9);
    	queue.put(emp10);
    	queue.put(emp4);
    	queue.put(emp5);
    	queue.put(emp6);
    	queue.put(emp1);
    	queue.put(emp2);
    	queue.put(emp3);
    	
    }
    
    /**
     * Get messages quantity from console
     * Only is possible set an integer between 1 and 100
     * The maximum ideal messages are 10
     * @return
     */
    private static int getMessagesQuantity(){
    	int messageQuantity = 0;
    	boolean numberIsOk = false;
    	while(!numberIsOk){
	    	try{
		    	//set quantity of messages
		    	Scanner scan = new Scanner(System.in);
		    	System.out.println("How many messages do you want to process?  Input an integer...");
		    	messageQuantity = scan.nextInt();
		    	if(messageQuantity <= 0 || messageQuantity > 100) throw new Exception("Out of range number...");
		    	numberIsOk = true;
	    	}catch(Exception e){
	    		System.out.println(e.getMessage()+" Could you enter an integer between 1 and 100?  Try again!");
	    	}
    	}
    	return messageQuantity;
    }
    
    /**
     * Set messages in an array.  
     * If messageQuantity is more than Dispatcher can process, the message will be processed with delay
     * @param messages
     * @param messageQuantity
     */
    private static void setMessagesToProcess(List <Message>messages, int messageQuantity){
    	
    	for(int i=1; i<=messageQuantity; i++){
    		Message msg = new Message("Mensaje "+i);
    		if(i <= DispatchMessagesConstants.maxQuantityMessagesToProcess){
    			msg.setProcessType(MessageProcessType.IMMEDIATLY);
    		}else{
    			msg.setProcessType(MessageProcessType.DELAYED);
    		}
    		messages.add(msg);
    	}
    	
    }
    
    /**
     * Comparator that order employees by their position in the company
     * in order to assign pending messages to be served
     */
    public static Comparator <Employee> employeeComparator = new Comparator <Employee> (){
    	//@Override
		public int compare(Employee emp1, Employee emp2) {
			if(emp1.getEmployeeType().getEmployeeType() < emp2.getEmployeeType().getEmployeeType()){
				return -1;
			}else if(emp1.getEmployeeType().getEmployeeType() > emp2.getEmployeeType().getEmployeeType()){
				return 1;
			}
			return 0;
		}   	
    };
    
    
}
