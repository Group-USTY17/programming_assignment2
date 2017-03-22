package com.ru.usty.scheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.ru.usty.scheduling.process.Process;
import com.ru.usty.scheduling.process.ProcessExecution;

public class Scheduler {
	
	public static final int FEEDBACK_QUEUE_LEVEL = 7;

	ProcessExecution processExecution;
	Policy policy;
	int quantum;
	Timer currentTime;
	
	private long startSchedulingTime;
	private int currentFBLevel;
	private int processCount;
	private Process mCurrentProcess;
	private final ArrayList<Process> mProcesses = new ArrayList<Process>();
	private final ArrayList<ArrayList<Process>> feedbackQueue = new ArrayList<ArrayList<Process>>(FEEDBACK_QUEUE_LEVEL);
	private final ArrayList<Long> turnaroundTimes = new ArrayList<Long>();
	private final ArrayList<Long> responseTimes = new ArrayList<Long>();
	private HashMap<Integer, Long> arrivalTimes = new HashMap<Integer, Long>();

	
	public Scheduler(ProcessExecution processExecution) {
		this.processExecution = processExecution;
	}

	/**
	 * DO NOT CHANGE DEFINITION OF OPERATION
	 */
	public void startScheduling(Policy policy, int quantum) {
		this.policy = policy;
		this.quantum = quantum;
		startSchedulingTime = System.currentTimeMillis();
		
		if (currentTime != null) {
			currentTime.cancel();
		}
		
		if(!turnaroundTimes.isEmpty() && !responseTimes.isEmpty()) {
			long avgTurnaroundTime = getAvg(turnaroundTimes);
			long avgResponseTime = getAvg(responseTimes);
			System.out.println("======================FINISHED SCHEDULE===================");
			System.out.println("Average turnaround time:" + avgTurnaroundTime + " ms");
			System.out.println("Average response time:" + avgResponseTime + " ms" );
			System.out.println("==========================================================");
			System.out.println();
			turnaroundTimes.clear();
			responseTimes.clear();
		}
		
		arrivalTimes.clear();
		mProcesses.clear();
		
		switch(policy) {
		case FCFS:	//First-come-first-served
			System.out.println("Starting new scheduling task: First-come-first-served");
			break;
			
		case RR:	//Round robin
			System.out.println("Starting new scheduling task: Round robin, quantum = " + quantum);
			break;
			
		case SPN:	//Shortest process next	
			System.out.println("Starting new scheduling task: Shortest process next");
			break;
			
		case SRT:	//Shortest remaining time
			System.out.println("Starting new scheduling task: Shortest remaining time");
			break;
			
		case HRRN:	//Highest response ratio next
			System.out.println("Starting new scheduling task: Highest response ratio next");
			break;
			
		case FB:	//Feedback
			System.out.println("Starting new scheduling task: Feedback, quantum = " + quantum);
			initFBQueue();
			break;
		}
		
	}

	/**
	 * DO NOT CHANGE DEFINITION OF OPERATION
	 */
	public void processAdded(int processID) {
		long arrivalTime = getCurrentAbsoluteTime();
		// Arrival time
		arrivalTimes.put(processID, arrivalTime);
		
		System.out.println("Process #" + processID + " arrived at:" + arrivalTime);		
		
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
		long completionTime = getCurrentAbsoluteTime(); 
		long turnaroundTime = completionTime - arrivalTimes.get(processID);
		turnaroundTimes.add(turnaroundTime);
		
		System.out.println("Process #" + processID + " finished at:" + completionTime + ", TAT:" + turnaroundTime);		
		
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
		removeProcessById(processID);
		
		if (!mProcesses.isEmpty()) {
			mCurrentProcess = mProcesses.get(0);
			switchToProcess(mCurrentProcess.getID());
			scheduleTimerRR();
		}
		else {
			mCurrentProcess = null;
		}
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
				if(mProcesses.size() > 1) {
					if(mCurrentProcess != null) {
						moveCurrentProcessToTail();
					}
					mCurrentProcess = mProcesses.get(0);
					switchToProcess(mCurrentProcess.getID());
				}
			}
		}, 0, quantum);
	}
	
	private void moveCurrentProcessToTail() {
		mProcesses.add(mProcesses.remove(0));
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
		Process process = new Process(processID, quantum);
		addToFBQueue(process, 0);
		processCount++;
		if(processCount == 1) {
			mCurrentProcess = process;
			switchToProcess(processID);
			scheduleTimerFB();
		}
	}
	
	private void processFinishedFB(int processID) {	
		removeProcessByID_FB(processID);
		processCount--;
		
		getNextProcessFB();
		if(mCurrentProcess != null) {
			switchToProcess(mCurrentProcess.getID());
			scheduleTimerFB();
		}
	}
	
	private void scheduleTimerFB() {
		if (currentTime != null) {
			currentTime.cancel();
		}
		currentTime = new Timer();
		currentTime.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (feedbackQueue.isEmpty()) {
					return;
				}				
				downgradeProcess(mCurrentProcess);
				getNextProcessFB();
				if(processCount > 1) {	
					if(mCurrentProcess == null) {
						return;
					}
					System.out.println("Next process #" + mCurrentProcess.getID() + ", at level:" + currentFBLevel);
					switchToProcess(mCurrentProcess.getID());
				}
			}
		}, 0, quantum);
	}
	
	private void getNextProcessFB(){
		int levelDepth = 0;
		for(ArrayList<Process> level : feedbackQueue) {
			if(!level.isEmpty()) {
				mCurrentProcess = level.get(0);
				currentFBLevel = levelDepth;
				return;
			}
			levelDepth++;
		}
		
		currentFBLevel = 0;
		mCurrentProcess = null;
	}
	
	private void addToFBQueue(Process process, int level) {
		feedbackQueue.get(level).add(process);
	}
	
	private void downgradeProcess(Process process) {
		int nextLevel = currentFBLevel+1;
		if(nextLevel >= FEEDBACK_QUEUE_LEVEL-1) {
			nextLevel = FEEDBACK_QUEUE_LEVEL-1;
		}
		feedbackQueue.get(currentFBLevel).remove(process);
		feedbackQueue.get(nextLevel).add(process);
	}
	
	private void initFBQueue() {
		currentFBLevel = 0;
		processCount = 0;
		for(int i = 0; i < FEEDBACK_QUEUE_LEVEL; i++) {
			feedbackQueue.add(new ArrayList<Process>());
		}
	}
	
	private void removeProcessByID_FB(int processID) {
		Process process;
		Iterator<Process> iterator = feedbackQueue.get(currentFBLevel).iterator();
		while (iterator.hasNext()) {
			process = iterator.next();
			if (process.getID() == processID) {
				iterator.remove();
				return;
			}
		}
	}
	
	
	/////////////////////////////////////////////////
	/// OTHER HELPER FUNCTIONS
	//////////////////////////////////////////////////
	
	// Switch between processes
	private void switchToProcess(int processID) {
		long switchTime = getCurrentAbsoluteTime();
		if(processExecution.getProcessInfo(processID).elapsedExecutionTime == 0) {
			long responseTime = switchTime - arrivalTimes.get(processID);
			responseTimes.add(responseTime);
			System.out.println("Starting process #" + processID + " at:" + switchTime + ", RT:" + responseTime);
		}
		else {
			System.out.println("Switching to process #" + processID + " at:" + switchTime);
		}
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
	
	private long getCurrentAbsoluteTime() {
		return System.currentTimeMillis() - startSchedulingTime;
	}
	
	private long getAvg(ArrayList<Long> list) {
		if(!list.isEmpty()) {
			long sum = 0;
			for(Long time : list) {
				sum += time;
			}
			return sum / list.size();
		}
		return -1;
	}
	
}
