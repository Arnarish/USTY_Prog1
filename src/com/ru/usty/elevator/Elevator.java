package com.ru.usty.elevator;



public class Elevator implements Runnable{
	int currFloor, maxFloor, passangers;

	
	public Elevator(int currFloor, int maxFloor, int passangersTotal) {
		this.currFloor = currFloor;
		this.maxFloor = maxFloor;
		this.passangers = passangersTotal;
		
	}
	
	public void run() {
		System.out.println("Elevator running");
	}
}
