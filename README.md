This program dispatch and serve recurrent messages.

Program can process from 1 to 100 messages.  The ideal number is 10.  You have to input the quantity by console.
Program initialize a priority queue with employees.  Than employees can be operators, supervisors o directors.  Queue is ordered since minor position to mayor position in the company.
I could make a class Employee and subclasses for Operator, Supervisor and Director, but i suppose that for this exercise that would be too much.
Program start n threads of Dispatch class (n = quantity of messages).  
Dispatch inherits from Thread.  I choose than and not Runnable, because Thread class have more methods for control multithreading.
Then program call to start method of Dispatch, and this method to run.

- When queue is empty, i put a sleep with a time and retry the process.

- When you input a message quantity great than 10, fist 10 threads (messages) have the maximum priority and the rest less priority of them.  Threads with less priority have a mark (delayed).  Threads with delayed mark execute method yield for wait and put again to ready to execute while others threads end to run.

***I didn’t use before threads in Java, i used gears with Grails, bug i know class Thread and read theory for make the exercise.  I would like discuss the exercise with you.  Thanks.
