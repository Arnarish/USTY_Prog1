package com.ru.usty.elevator;

public class Person implements Runnable{
	int source, dest; //start and end floors
	//int elevator; //keep track of the elevator this person enters
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
			ElevatorScene.sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Person Thread");
	}
}
