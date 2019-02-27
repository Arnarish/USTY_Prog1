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
			//TODO
			//if person is going up, aquire goingUp semaphore on source floor
			//else aquire goingDown semaphorer on soure floor
			
			//decrement the number of people waiting at source floor
			//increment the passenger count of the elevator
			
			//waiting in elevator mutex?
			if (goingUp) {
				ElevatorScene.goingUp.get(source).acquire();
			}
			else {
				ElevatorScene.goingDown.get(source).acquire();
			}
			this.elevator = ElevatorScene.elevatorOpen;
			
			
			ElevatorScene.eScene.decPeopleWaiting(source, goingUp);
			ElevatorScene.eScene.incPeopleInElevator(elevator);
			
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ElevatorScene.eScene.personExitsAtFloor(dest, elevator);
		ElevatorScene.eScene.decPeopleInElevator(elevator);
	}
}
