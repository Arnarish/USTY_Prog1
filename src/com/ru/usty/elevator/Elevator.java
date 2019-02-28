package com.ru.usty.elevator;



public class Elevator implements Runnable{
	int elevatorID, currFloor, maxFloor, capacity, passangers;
	
	public Elevator(int elevatorID, int currFloor, int capacity, int passangersTotal) {
		this.elevatorID = elevatorID;
		this.currFloor = currFloor;
		this.maxFloor = maxFloor;
		this.capacity = ElevatorScene.ELEVATOR_MAX;
		this.passangers = passangersTotal;
	}
	
	//int currFloor, maxFloor, passangers;
	//Vector<Person> passangersInLift;
	//Semaphore doorLock;
	
	
	public void run() {
		while(true) {
			System.out.println("Elevator running");
			
			
		}
	}
}
