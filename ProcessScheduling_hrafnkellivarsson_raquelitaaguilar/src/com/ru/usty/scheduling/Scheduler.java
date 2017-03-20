package com.ru.usty.scheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.ru.usty.scheduling.process.Process;
import com.ru.usty.scheduling.process.ProcessExecution;

public class Scheduler {

	ProcessExecution processExecution;
	Policy policy;
	int quantum;
	Timer currentTime;
	private final ArrayList<Process> mProcesses = new ArrayList<Process>();

	
	public Scheduler(ProcessExecution processExecution) {
		this.processExecution = processExecution;
	}

	/**
	 * DO NOT CHANGE DEFINITION OF OPERATION
	 */
	public void startScheduling(Policy policy, int quantum) {
		this.policy = policy;
		this.quantum = quantum;

		switch(policy) {
		case FCFS:	//First-come-first-served
			System.out.println("Starting new scheduling task: First-come-first-served");
			mProcesses.clear();
			break;
			
		case RR:	//Round robin
			System.out.println("Starting new scheduling task: Round robin, quantum = " + quantum);
			mProcesses.clear();
			break;
			
		case SPN:	//Shortest process next
			if (currentTime != null) {
				currentTime.cancel();
			}
			System.out.println("Starting new scheduling task: Shortest process next");
			mProcesses.clear();
			break;
			
		case SRT:	//Shortest remaining time
			System.out.println("Starting new scheduling task: Shortest remaining time");
			mProcesses.clear();
			break;
			
		case HRRN:	//Highest response ratio next
			System.out.println("Starting new scheduling task: Highest response ratio next");
			mProcesses.clear();
			break;
			
		case FB:	//Feedback
			System.out.println("Starting new scheduling task: Feedback, quantum = " + quantum);
			mProcesses.clear();
			break;
		}
	}

	/**
	 * DO NOT CHANGE DEFINITION OF OPERATION
	 */
	public void processAdded(int processID) {
		
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
	// FCFS - PRIVATE FUNCTIONS
	///////////////////////////////////////////////////////
	
	private void processAddedFCFS(int processID) {		
		Process process = new Process(processID, quantum);
		mProcesses.add(process);
		if(mProcesses.size() == 1) {
			switchToProcess(processID);
		}
	}
	
	private void processFinishedFCFS(int processID) {
		removeProcessById(processID);
		if(!mProcesses.isEmpty()) {
			switchToProcess(mProcesses.get(0).getID());
		}
	}
	
	////////////////////////////////////////////////////////
	// RR - PRIVATE FUNCTIONS
	///////////////////////////////////////////////////////
	
	private Process mCurrentProcess;
	
	private void processAddedRR(int processID) {
		Process process = new Process(processID, quantum);
		mProcesses.add(process);
		if(mProcesses.size() == 1) {
			mCurrentProcess = process;
			switchToProcess(processID);
			scheduleTimerRR();
		}
	}
	
	private void processFinishedRR(int processID) {
		Process process = getProcessById(processID);
		int processIndex = mProcesses.indexOf(process);
		
		removeProcessById(processID);
		
		if (mProcesses.isEmpty()) {
			return;
		}
		if (processIndex >= mProcesses.size()) {
			processIndex = 0;
		}
		mCurrentProcess = mProcesses.get(processIndex);
		
		switchToProcess(mCurrentProcess.getID());
		scheduleTimerRR();
	}
	
	// 
	private void scheduleTimerRR() {
		if (currentTime != null) {
			currentTime.cancel();
		}
		currentTime = new Timer();
		currentTime.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (mProcesses.isEmpty()) {
					return;
				}
				if(mCurrentProcess != null){
					getNextProcessRR();
				} else{
					mCurrentProcess = mProcesses.get(0);
				}
				switchToProcess(mCurrentProcess.getID());
			}
		}, 0, quantum);
	}
	
	private void getNextProcessRR(){
		int currentIndex = mProcesses.indexOf(mCurrentProcess) + 1;
		if (currentIndex >= mProcesses.size()) {
			currentIndex = 0;
		}
		if(!mProcesses.isEmpty()){
			mCurrentProcess = mProcesses.get(currentIndex);
		} else{
			mCurrentProcess = null;
		}
	}
	
	////////////////////////////////////////////////////////
	// SPN - PRIVATE FUNCTIONS
	///////////////////////////////////////////////////////
	
	private void processAddedSPN(int processID) {
		Process process = new Process(processID, quantum);
		mProcesses.add(process);
		if(mProcesses.size() == 1) {
			switchToProcess(processID);
		}
	}
	
	private void processFinishedSPN(int processID) {
		removeProcessById(processID);
		if(!mProcesses.isEmpty()) {
			switchToProcess(getShortestProcessID());
		}
	}
	
	private int getShortestProcessID() {
		int shortestProcessID = -1, processID;
		long shortestServiceTime = Long.MAX_VALUE, currentServiceTime;
		Process process;
		
		Iterator<Process> iterator = mProcesses.iterator();
		while (iterator.hasNext()) {
			process = iterator.next();
			processID = process.getID();
			currentServiceTime = processExecution.getProcessInfo(processID).totalServiceTime;
			
			if (currentServiceTime < shortestServiceTime) {
				shortestServiceTime = currentServiceTime;
				shortestProcessID = processID;
			}
		}
		
		return shortestProcessID;
	}
	
	////////////////////////////////////////////////////////
	// SRT - PRIVATE FUNCTIONS
	///////////////////////////////////////////////////////
	
	private void processAddedSRT(int processID) {
		Process process = new Process(processID, quantum);
		mProcesses.add(process);
		if(mProcesses.size() == 1) {
			switchToProcess(processID);
		}
		else if(getShortestRemainingTimeID() == processID) {
			switchToProcess(processID);
		}
	}
	
	private void processFinishedSRT(int processID) {
		removeProcessById(processID);
		if(!mProcesses.isEmpty()) {
			switchToProcess(getShortestRemainingTimeID());
		}
	}
	
	private int getShortestRemainingTimeID() {
		int shortestProcessID = -1, processID;
		long shortestRemainingTime = Long.MAX_VALUE, currentRemainingTime, currentTotalServiceTime, currentElapsedExecutionTime;
		Process process;
		
		Iterator<Process> iterator = mProcesses.iterator();
		while (iterator.hasNext()) {
			process = iterator.next();
			processID = process.getID();
			currentTotalServiceTime = processExecution.getProcessInfo(processID).totalServiceTime;
			currentElapsedExecutionTime = processExecution.getProcessInfo(processID).elapsedExecutionTime;
			currentRemainingTime = currentTotalServiceTime - currentElapsedExecutionTime;
			
			if (currentRemainingTime < shortestRemainingTime) {
				shortestRemainingTime = currentRemainingTime;
				shortestProcessID = processID;
			}
		}
		
		return shortestProcessID;
	}
	
	////////////////////////////////////////////////////////
	// HRRN - PRIVATE FUNCTIONS
	///////////////////////////////////////////////////////
	
	private void processAddedHRRN(int processID) {
		Process process = new Process(processID, quantum);
		mProcesses.add(process);
		if(mProcesses.size() == 1) {
			switchToProcess(processID);
		}
	}
	
	private void processFinishedHRRN(int processID) {
		removeProcessById(processID);
		if(!mProcesses.isEmpty()) {
			switchToProcess(getHRRNPriorityID());
		}
	}
	
	private long calculateHRRNPriority(int processID) {
		long waitingTime = processExecution.getProcessInfo(processID).elapsedWaitingTime;
		long serviceTime = processExecution.getProcessInfo(processID).totalServiceTime;
		
		return (waitingTime + serviceTime) / serviceTime;
	}
	
	private int getHRRNPriorityID() {
		int priorityProcessID = -1, processID;
		long highestPriority = Long.MIN_VALUE, currentPriority;
		Process process;
		
		Iterator<Process> iterator = mProcesses.iterator();
		while (iterator.hasNext()) {
			process = iterator.next();
			processID = process.getID();
			currentPriority = calculateHRRNPriority(processID);
			
			if (currentPriority > highestPriority) {
				highestPriority = currentPriority;
				priorityProcessID = processID;
			}
		}
		
		return priorityProcessID;
	}	
	
	
	////////////////////////////////////////////////////////
	// FB - PRIVATE FUNCTIONS
	///////////////////////////////////////////////////////
	
	private void processAddedFB(int processID) {
		//TODO
	}
	
	private void processFinishedFB(int processID) {
		//TODO
	}	
	
	
	/////////////////////////////////////////////////
	/// OTHER HELPER FUNCTIONS
	//////////////////////////////////////////////////
	
	// Switch between processes
	private void switchToProcess(int processID) {
		System.out.println("Switching to process ID:" + processID);
		processExecution.switchToProcess(processID);
	}
	
	// Get the process by ID
	private Process getProcessById(int processId) {
		for (Process process : mProcesses) {
			if (process.getID() == processId) {
				return process;
			}
		}
		return null;
	}
	
	// Removing the process by its ID
	private void removeProcessById(int processId) {
		Process process;
		Iterator<Process> iterator = mProcesses.iterator();
		while (iterator.hasNext()) {
			process = iterator.next();
			if (process.getID() == processId) {
				iterator.remove();
				return;
			}
		}
	}
	
}
