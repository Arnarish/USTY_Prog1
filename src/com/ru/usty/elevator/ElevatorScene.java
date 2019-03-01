package com.ru.usty.elevator;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * The base function definitions of this class must stay the same
 * for the test suite and graphics to use.
 * You can add functions and/or change the functionality
 * of the operations at will.
 *
 */

public class ElevatorScene {

	//TO SPEED THINGS UP WHEN TESTING,
	//feel free to change this.  It will be changed during grading
	public static final int VISUALIZATION_WAIT_TIME = 400;  //milliseconds
	public static final int ELEVATOR_MAX = 6; //maximum allowed passengers in the elevator
	public static final int ELEVATOR_MAX_TOPBOT = 4; //to prevent starving in middle floors, don't allow filling the elevator at min/max floors
	
	public static ElevatorScene eScene; //allows the threads to communicate and modify required parameters
	
	private int numberOfFloors;
	private int numberOfElevators;
	
	public static ArrayList<Semaphore> exitedCountMutex; //list in case of multiple elevators
	public static ArrayList<Semaphore> goingUp;
	public static ArrayList<Semaphore> goingDown;
	public static ArrayList<Semaphore> inElevatorMutex;
	public static ArrayList<ArrayList<Semaphore>> exitFloors;
	public static Semaphore personCMutex; 
	public static Semaphore elevatorOpenMutex; //no more than one elevator open at once
	
	public static Integer elevatorOpen; 
	public static ArrayList<Integer> currFloor; //list of where all the elevators are located
	public static ArrayList<Integer> peopleInElevator; //list of people in elevators
	public ArrayList<Boolean> elevatorGoingUp; //is the elevator going up or down

	ArrayList<Integer> personCount; //use if you want but
									//throw away and
									//implement differently
									//if it suits you
	ArrayList<Integer> exitedCount = null;
	ArrayList<Integer> personsUp; //people going up
	ArrayList<Integer> personsDown; //people going down
	
	private Thread elevatorThread = null;

	//Base function: definition must not change
	//Necessary to add your code in this one
	public void restartScene(int numberOfFloors, int numberOfElevators) {

		//missing method to stop a run if there are threads running currently?
		
		/*new Thread(new Runnable() { commented out by SDG, merge conflict
			public void run() {
				for(int i = 0; i < 20; i++) {
					sem.release();
					System.out.println("Permits " + sem.availablePermits());
				}	
			}
		}).start();
		
		//Elevator elevator = new Elevator(0, 0, numberOfFloors, 0, null, sem);
		//Elevator elevator = new Elevator(0, numberOfFloors, 0, null, sem);
		
		
		//sem.release();*/

		/**
		 * Important to add code here to make new
		 * threads that run your elevator-runnables
		 * 
		 * Also add any other code that initializes
		 * your system for a new run
		 * 
		 * If you can, tell any currently running
		 * elevator threads to stop
		 */
		
		elevatorOpen = null;
		eScene = this;

		this.numberOfFloors = numberOfFloors;
		this.numberOfElevators = numberOfElevators;
		//initialize all semaphores for a new run
		personCMutex = new Semaphore(1);
		elevatorOpenMutex = new Semaphore(1);
		inElevatorMutex = new ArrayList<Semaphore>();
		goingDown = new ArrayList<Semaphore>();
		goingUp = new ArrayList<Semaphore>();
		exitedCountMutex = new ArrayList<Semaphore>();
		exitFloors = new ArrayList<ArrayList<Semaphore>>();
		//initialize lists for a new run
		personsUp = new ArrayList<Integer>();
		personsDown = new ArrayList<Integer>();
		personCount = new ArrayList<Integer>();
		peopleInElevator = new ArrayList<Integer>();
		elevatorGoingUp = new ArrayList<Boolean>();
		currFloor = new ArrayList<Integer>();
		//below, we loop through the number of floors/elevators as appropriate and add each elevator/floor as relevant for a new run.
		for(int i = 0; i < numberOfFloors; i++) {
			this.personCount.add(0);
			this.personsUp.add(0);
			this.personsDown.add(0);
		}
		
		for(int i = 0; i < numberOfElevators; i++) {
			exitFloors.add(new ArrayList<Semaphore>());
			inElevatorMutex.add(new Semaphore(0));
			for(int j=0; j < numberOfFloors; j++) {
				exitFloors.get(i).add(new Semaphore(0));
			}
		}

		if(exitedCount == null) {
			exitedCount = new ArrayList<Integer>();
		}
		else {
			exitedCount.clear();
		}
		for(int i = 0; i < getNumberOfFloors(); i++) {
			this.exitedCount.add(0);
			goingUp.add(new Semaphore(0));
			goingDown.add(new Semaphore(0));
		}
		//initialize the elevators
		for(int i = 0; i < numberOfElevators; i++) {
			int startfloor = 0; // could be random if we want to initialize on a random floor
			peopleInElevator.add(0);
			elevatorThread = new Thread(new Elevator(i, startfloor, ELEVATOR_MAX, peopleInElevator.get(i))); //nobody starts inside the elevator
			elevatorGoingUp.add(true); //we're starting on the ground floor, only way is up
			currFloor.add(startfloor);
			exitedCountMutex.add(new Semaphore(1));
			elevatorThread.start();
		}
	}

	//Base function: definition must not change
	//Necessary to add your code in this one
	public Thread addPerson(int sourceFloor, int destinationFloor) {

		/**
		 * Important to add code here to make a
		 * new thread that runs your person-runnable
		 * 
		 * Also return the Thread object for your person
		 * so that it can be reaped in the testSuite
		 * (you don't have to join() yourself)
		 */
		
		Person person = new Person(sourceFloor, destinationFloor);
		Thread p_thread = new Thread(person);
		
		p_thread.start();
		
		if(person.goingUp) {
			ElevatorScene.eScene.incPeopleWaiting(sourceFloor, true);
		}
		else {
			ElevatorScene.eScene.incPeopleWaiting(sourceFloor, false);
		}
		//dumb code, replace it!
		return p_thread;  //this means that the testSuite will not wait for the threads to finish
	}
	
	public void nextFloor(int elevator) {
		if (elevatorGoingUp.get(elevator) && getNumberOfPeopleInElevator(elevator) == 0) {
			//elevatorGoingUp.set(elevator, false);
			//System.out.println("I'm empty and going up");
			for(int i = getCurrentFloorForElevator(elevator); i > 0; i--) {
				if(isButtonPushedAtFloor(i)) {
					System.out.println("I'm empty and someone is below me. Going down.");
					elevatorGoingUp.set(elevator, false);
				}
			}
		}
		else if(!elevatorGoingUp.get(elevator) && getNumberOfPeopleInElevator(elevator) == 0) {
			//elevatorGoingUp.set(elevator, true);
			//System.out.println("I'm empty and going down");
			for(int i=getCurrentFloorForElevator(elevator); i < getNumberOfFloors(); i++) {
				if(isButtonPushedAtFloor(i)) {
					System.out.println("I'm empty and someone is above me. Going up.");
					elevatorGoingUp.set(elevator, true);
				}
			}
		}
		//make sure the elevator can't go out of bounds
		if(ElevatorScene.currFloor.get(elevator) >= (this.numberOfFloors -1)) {
			elevatorGoingUp.set(elevator, false);
		}
		else if(ElevatorScene.currFloor.get(elevator) < 1) {
			elevatorGoingUp.set(elevator, true);
		}
		
		if(elevatorGoingUp.get(elevator)) {
			currFloor.set(elevator, (currFloor.get(elevator) +1));
			if(ElevatorScene.currFloor.get(elevator) >= (this.numberOfFloors -1)) {
				elevatorGoingUp.set(elevator, false);
			}
		}
		else {
			currFloor.set(elevator, (currFloor.get(elevator) -1));
			if(ElevatorScene.currFloor.get(elevator) < 1) {
				elevatorGoingUp.set(elevator, true);
			}
		}
	}
	
	
	public void incPeopleInElevator(int elevator) {
		try {
			personCMutex.acquire();
			peopleInElevator.set(elevator, (getNumberOfPeopleInElevator(elevator) +1));
			personCMutex.release();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void decPeopleInElevator(int elevator) {
		try {
			personCMutex.acquire();
			peopleInElevator.set(elevator, (getNumberOfPeopleInElevator(elevator) -1));
			personCMutex.release();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void incPeopleWaiting(int floor, boolean goingUp) {
		try {
			personCMutex.acquire();
			personCount.set(floor, (personCount.get(floor)+1));
			if(goingUp) {
				personsUp.set(floor, (personsUp.get(floor)+1));
			}
			else {
				personsDown.set(floor, (personsDown.get(floor)+1));
			}
			personCMutex.release();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void decPeopleWaiting(int floor, boolean goingUp) {
		try {
			personCMutex.acquire();
			personCount.set(floor, (personCount.get(floor)-1));
			if(goingUp) {
				personsUp.set(floor, (personsUp.get(floor)-1));
			}
			else {
				personsDown.set(floor, (personsDown.get(floor)-1));
			}
			personCMutex.release();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	//Base function: definition must not change, but add your code
	public int getCurrentFloorForElevator(int elevator) {

		//dumb code, replace it!
		return currFloor.get(elevator);
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleInElevator(int elevator) {
		
		//dumb code, replace it!
		return peopleInElevator.get(elevator);
	}

	//Base function: definition must not change, but add your code
	public int getNumberOfPeopleWaitingAtFloor(int floor) {

		return personCount.get(floor);
	}

	//Base function: definition must not change, but add your code if needed
	public int getNumberOfFloors() {
		return numberOfFloors;
	}

	//Base function: definition must not change, but add your code if needed
	public void setNumberOfFloors(int numberOfFloors) {
		this.numberOfFloors = numberOfFloors;
	}

	//Base function: definition must not change, but add your code if needed
	public int getNumberOfElevators() {
		return numberOfElevators;
	}

	//Base function: definition must not change, but add your code if needed
	public void setNumberOfElevators(int numberOfElevators) {
		this.numberOfElevators = numberOfElevators;
	}

	//Base function: no need to change unless you choose
	//				 not to "open the doors" sometimes
	//				 even though there are people there
	public boolean isElevatorOpen(int elevator) {

		return isButtonPushedAtFloor(getCurrentFloorForElevator(elevator));
	}
	//Base function: no need to change, just for visualization
	//Feel free to use it though, if it helps
	public boolean isButtonPushedAtFloor(int floor) {

		return (getNumberOfPeopleWaitingAtFloor(floor) > 0);
	}

	//Person threads must call this function to
	//let the system know that they have exited.
	//Person calls it after being let off elevator
	//but before it finishes its run.
	public void personExitsAtFloor(int floor, int elevator) {
		try {
			
			exitedCountMutex.get(elevator).acquire();
			exitedCount.set(floor, (exitedCount.get(floor) + 1));
			exitedCountMutex.get(elevator).release();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Base function: no need to change, just for visualization
	//Feel free to use it though, if it helps
	public int getExitedCountAtFloor(int floor) {
		if(floor < getNumberOfFloors()) {
			return exitedCount.get(floor);
		}
		else {
			return 0;
		}
	}


}
