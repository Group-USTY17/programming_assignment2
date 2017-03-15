package com.ru.usty.scheduling;

import java.util.ArrayList;

import com.ru.usty.scheduling.process.ProcessExecution;

public class Scheduler {

	ProcessExecution processExecution;
	Policy policy;
	int quantum;

	/**
	 * Add any objects and variables here (if needed)
	 */
	
	ArrayList<Integer> processQueue;

	/**
	 * DO NOT CHANGE DEFINITION OF OPERATION
	 */
	public Scheduler(ProcessExecution processExecution) {
		this.processExecution = processExecution;

		/**
		 * Add general initialization code here (if needed)
		 */
	}

	/**
	 * DO NOT CHANGE DEFINITION OF OPERATION
	 */
	public void startScheduling(Policy policy, int quantum) {

		this.policy = policy;
		this.quantum = quantum;

		/**
		 * Add general initialization code here (if needed)
		 */

		switch(policy) {
		case FCFS:	//First-come-first-served
			System.out.println("Starting new scheduling task: First-come-first-served");
			/**
			 * Add your policy specific initialization code here (if needed)
			 */
			processQueue = new ArrayList<Integer>();
			break;
		case RR:	//Round robin
			System.out.println("Starting new scheduling task: Round robin, quantum = " + quantum);
			/**
			 * Add your policy specific initialization code here (if needed)
			 */
			break;
		case SPN:	//Shortest process next
			System.out.println("Starting new scheduling task: Shortest process next");
			/**
			 * Add your policy specific initialization code here (if needed)
			 */
			break;
		case SRT:	//Shortest remaining time
			System.out.println("Starting new scheduling task: Shortest remaining time");
			/**
			 * Add your policy specific initialization code here (if needed)
			 */
			break;
		case HRRN:	//Highest response ratio next
			System.out.println("Starting new scheduling task: Highest response ratio next");
			/**
			 * Add your policy specific initialization code here (if needed)
			 */
			break;
		case FB:	//Feedback
			System.out.println("Starting new scheduling task: Feedback, quantum = " + quantum);
			/**
			 * Add your policy specific initialization code here (if needed)
			 */
			break;
		}

		/**
		 * Add general scheduling or initialization code here (if needed)
		 */

	}

	/**
	 * DO NOT CHANGE DEFINITION OF OPERATION
	 */
	public void processAdded(int processID) {

		/**
		 * Add scheduling code here
		 */
		System.out.println("Process added, ID:" + processID);
		
		
		switch(policy) {
		case FCFS:	//First-come-first-served
			processAddedFCFS(processID);
			break;
		case RR:	//Round robin
			processAddedRR(processID);
			break;
		case SPN:	//Shortest process next
			processAddedSPN(processID);
			break;
		case SRT:	//Shortest remaining time
			processAddedSRT(processID);
			break;
		case HRRN:	//Highest response ratio next
			processAddedHRRN(processID);
			break;
		case FB:	//Feedback
			processAddedFB(processID);
			break;
		}
		

	}

	/**
	 * DO NOT CHANGE DEFINITION OF OPERATION
	 */
	public void processFinished(int processID) {

		/**
		 * Add scheduling code here
		 */
		System.out.println("Process finished, ID:" + processID);
		
		switch(policy) {
		case FCFS:	//First-come-first-served
			processFinishedFCFS(processID);
			break;
		case RR:	//Round robin
			processFinishedRR(processID);
			break;
		case SPN:	//Shortest process next
			processFinishedSPN(processID);
			break;
		case SRT:	//Shortest remaining time
			processFinishedSRT(processID);
			break;
		case HRRN:	//Highest response ratio next
			processFinishedHRRN(processID);
			break;
		case FB:	//Feedback
			processFinishedFB(processID);
			break;
		}

	}
	
	////////////////////////////////////////////////////////
	//PROCESS ADDED PRIVATE FUNCTIONS
	///////////////////////////////////////////////////////
	
	private void processAddedFCFS(int processID) {
		//TODO
		processQueue.add(processID); //add new process to queue
		if(processQueue.size() <= 1) { //if its the only item in the queue process it right away
			switchToProcess(processID);
		}
		
	}
	
	private void processAddedRR(int processID) {
		//TODO
	}
	
	private void processAddedSPN(int processID) {
		//TODO
	}
	
	private void processAddedSRT(int processID) {
		//TODO
	}
	
	private void processAddedHRRN(int processID) {
		//TODO
	}
	
	private void processAddedFB(int processID) {
		//TODO
	}
	
	////////////////////////////////////////////////////////
	//PROCESS FINISHED PRIVATE FUNCTIONS
	///////////////////////////////////////////////////////
	
	private void processFinishedFCFS(int processID) {
		//TODO
		processQueue.remove(0); //remove the process from the queue
		if(!processQueue.isEmpty()) { //if there are more processes waiting, process them.
			switchToProcess(processQueue.get(0));
		}
	}
	
	private void processFinishedRR(int processID) {
		//TODO
	}
	
	private void processFinishedSPN(int processID) {
		//TODO
	}
	
	private void processFinishedSRT(int processID) {
		//TODO
	}
	
	private void processFinishedHRRN(int processID) {
		//TODO
	}
	
	private void processFinishedFB(int processID) {
		//TODO
	}	
	
	/////////////////////////////////////////////////
	/// OTHER HELPER FUNCTIONS
	//////////////////////////////////////////////////
	
	private void switchToProcess(int processID) {
		System.out.println("Switching to process ID:" + processID);
		processExecution.switchToProcess(processID);
	}
	
}
