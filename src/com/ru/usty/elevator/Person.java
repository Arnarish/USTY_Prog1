package com.ru.usty.elevator;

public class Person implements Runnable{
	int source, dest; //start and end floors
	int elevator; //keep track of the elevator this person enters
	boolean goingUp; //is the person going up or down floors
	Person(int source, int dest){
		this.source = source;
		this.dest = dest; 
		//boolean to track if person is going up or down with the elevator
		if(dest > source) {
			this.goingUp = true;
		}
		else {
			this.goingUp = false;
		}
	}
	
	public void run() {
		try {
			//person going up or down?
			if (goingUp) {
				ElevatorScene.goingUp.get(source).acquire();
			}
			else {
				ElevatorScene.goingDown.get(source).acquire();
			}
			//in case of multiple elevator options, keep track of what elevator this person is in
			this.elevator = ElevatorScene.elevatorOpen;
			System.out.println("Person: Entering elevator: " + elevator + " on floor " + ElevatorScene.eScene.getCurrentFloorForElevator(elevator));
			//person is in the elevator, remove from waiting and put in elevator
			ElevatorScene.eScene.decPeopleWaiting(source, goingUp);
			ElevatorScene.eScene.incPeopleInElevator(elevator);
			//use mutex to lock person in elevator, elevator releases person once it is on the correct floor
			ElevatorScene.inElevatorMutex.get(elevator).acquire();
			ElevatorScene.exitFloors.get(elevator).get(dest).acquire();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//person exits the elevator
		System.out.println("Person: Exiting at floor: " + dest);
		ElevatorScene.eScene.personExitsAtFloor(dest, elevator);
		ElevatorScene.eScene.decPeopleInElevator(elevator);
	}
}
