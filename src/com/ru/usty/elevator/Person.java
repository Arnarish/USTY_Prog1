package com.ru.usty.elevator;

public class Person implements Runnable{
	int source, dest; 
	Person(int source, int dest){
		this.source = source;
		this.dest = dest; 
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
