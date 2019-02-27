package com.ru.usty.elevator;



public class Elevator implements Runnable{
	int elevatorID, currFloor, maxFloor, capacity, passangers;
	
	public Elevator(int elevatorID, int currFloor, int maxFloor, int capacity, int passangersTotal) {
		this.elevatorID = elevatorID;
		this.currFloor = currFloor;
		this.maxFloor = maxFloor;
		this.capacity = capacity;
		this.passangers = passangersTotal;
	}
	
	//int currFloor, maxFloor, passangers;
	//Vector<Person> passangersInLift;
	//Semaphore doorLock;
	
	
	public void run() {
		System.out.println("Elevator running");
	}
}
