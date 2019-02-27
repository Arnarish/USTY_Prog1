package com.ru.usty.elevator;

import java.util.Vector;
import java.util.concurrent.Semaphore;

public class Elevator implements Runnable{
	int elevatorID, currFloor, maxFloor, capacity, passangers;
	Semaphore doorLock;
	
	public Elevator(int elevatorID, int currFloor, int maxFloor, int capacity, int passangersTotal, Semaphore doorLock) {
		this.elevatorID = elevatorID;
		this.currFloor = currFloor;
		this.maxFloor = maxFloor;
		this.capacity = capacity;
		this.passangers = passangersTotal;
		this.doorLock = doorLock;
	}
	
	//int currFloor, maxFloor, passangers;
	//Vector<Person> passangersInLift;
	//Semaphore doorLock;
	
	
	public void run() {
		System.out.println("Elevator running");
	}
}
