# programming_assignment2

## The Product
The product is a program, based on the base project provided on the project webpage on myschool, that simulates process scheduling. Six different scheduling policies will be implemented. The base functionality adds processes and simulates them running, as well as letting the scheduler know which policy to use each time. The student’s part of the program must process this information and make decisions on which process should run next and when they should be switched out.

The methods to be implemented are:
• **25%** - First Come First Served (FCFS)
• **20%** - Round Robin (RR)
• **10%** - Shortest Process Next (SPN)
• **10%** - Shortest Remaining Time (SRT)
• **10%** - Highest Response Ratio Next (HRRN) 
• **20%** - Feedback (FB)

In addition a PDF document should be returned, containing a short description of the implementation of each policy, with references to code, for example how time-slicing was implemented where that is needed, how ready-queues and/or other lists were implemented and what their purpose was. The report should also contain measurement data stating for each policy:

• Average Response Time
• Average Turnaround Time

The measurement data are **5%** of the final grade while the descriptions of the policies will be taken into the evaluation for each policy implemented.

## Getting and returning the assignment
On the project website on myschool there is an archive with a Java project which serves as the base for your program. Fetch, open and rename the project with the name of at least one member of your group. Once the program is ready, archive it (zip/rar/7z) and return along with a PDF with your results. The project can be done in groups of three (or less).

## The Program
Note that students should not change any of the classes in the base project except the class *Scheduler*. Of course it is allowed to add classes and objects to the heart’s content, as well as change *SchedulingMainProgram* to test specific things. All other files that are in the original java project should be left alone.
Also note that no function in the *Scheduler* class can block or run endless loops, as it is the main loop that runs the processes which calls those functions. The operations called on *Scheduler (startScheduling, processAdded* and *processFinished*) should set and initialize what they need, make a decision, call *switchToProcess* if needed and then finish. That way *Scheduler*, for the most part, works like a dispatcher, a small program which simply switches one process out for another.
When the program is started, it automatically makes an instance of *Scheduler* and sends it an instance of a class implementing the *ProcessExecution* interface. *ProcessExecution* has two operations that you can call, *getProcessInfo* and *switchToProcess*. Both take processID as an integer parameter. *getProcessInfo* returns an instance of *ProcessInfo* which holds the numbers *elapsedWaitingTime* (w), *elapsedExecutionTime* (e) and *totalServiceTime* (s). Explanations of these numbers and their use are in the book and the first lecture on scheduling.
Only if all processes in a test case are run to completion does the test case start over, now asking for the next policy.

## The Process
Try to change the function *processAdded* in your *Scheduler* class and, for example, for the first time it’s called, call *processExecution.switchToProcess(processID)*. See what happens now when the first process is added. Experiment with switching between processes to see what happens when they finish and when a process is preempted before it finishes.
Now implement all the policies. Use the numbers from *ProcessInfo* when needed and implement any lists and queues you need any way you feel works best. You will only be working with *processID* and allowing the *processExecution* system to handle the processes themselves.

The base program runs in a single thread, but you are welcome to use threads or other means to make the events you need for Scheduler to work as intended. The system itself calls *processAdded* and *processFinished* but you are welcome to implement more interruption operations and find ways to call them yourselves. Just make sure you don’t sleep, run endless loops or block in any way in the main thread, as that will block the entire simulation as well.

Have a good time!
