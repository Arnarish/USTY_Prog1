package com.ru.usty.elevator;

import java.util.Vector;
import java.util.concurrent.Semaphore;

public class Elevator implements Runnable{
	int currFloor, maxFloor, passangers;
	Vector<Person> passangersInLift;
	Semaphore doorLock;
	
	public Elevator(int currFloor, int maxFloor, int passangersTotal, Vector<Person> passangersInLift, Semaphore doorLock) {
		this.currFloor = currFloor;
		this.maxFloor = maxFloor;
		this.passangers = passangersTotal;
		this.passangersInLift = passangersInLift;
		this.doorLock = doorLock;
		
		
		
	}
	
	public void run() {
		System.out.println("Elevator running");
	}
}
