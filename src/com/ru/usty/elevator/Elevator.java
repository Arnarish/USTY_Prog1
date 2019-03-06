package com.ru.usty.elevator;



public class Elevator implements Runnable{
	int elevatorID, currFloor, capacity, passangers;
	
	public Elevator(int elevatorID, int capacity, int passangersTotal) {
		this.elevatorID = elevatorID; //identifier for the elevator
		this.capacity = ElevatorScene.ELEVATOR_MAX; //maximum capacity that the elevator can hold
		this.passangers = passangersTotal; //currently loaded passengers
	}
	
	public void run() {
		while(true) {
			try{
				if(ElevatorScene.eScene.stoprun) {
					//break the loop and terminate the thread
					return;
				}
				//first open the doors, not allowing other elevators to open.
				ElevatorScene.elevatorOpenMutex.get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).acquire();
				ElevatorScene.elevatorOpen.set(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID), elevatorID);
				
				//load/release passengers
				loadPassengers();
				Thread.sleep(400);
				releasePassengers();
				// close the door, and move on to the next floor
				ElevatorScene.elevatorOpenMutex.get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release();
				if(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID) > 0 || ElevatorScene.eScene.checkAny()) {
					ElevatorScene.eScene.nextFloor(elevatorID);
					ElevatorScene.eScene.floorTransition(elevatorID);
					Thread.sleep(400);
				}
				else {
					System.out.println("Nap for Elevator: " + elevatorID);
					Thread.sleep(400);
				}
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadPassengers() {
		try {
			//to prevent starvation, check middle floors if on base/top floor for potential passengers
			if(ElevatorScene.eScene.checkMiddle() && (ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID) == 0 || ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID) == ElevatorScene.eScene.getNumberOfFloors()-1)) {
				this.capacity = ElevatorScene.ELEVATOR_MAX_TOPBOT;
			}
			else {
				this.capacity = ElevatorScene.ELEVATOR_MAX;
			}
			//only accept passengers up to capacity
			int peopleWaiting = ElevatorScene.eScene.getNumberOfPeopleWaitingAtFloor(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID));
			int spaceAvailable = this.capacity-ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID);
			if (spaceAvailable < 0) {
				//reducing the limit to 3 while containing occupants can cause some issues, this should prevent that.
				System.out.println("Oops");
				spaceAvailable = 0;
			}
			if(ElevatorScene.eScene.elevatorGoingUp.get(elevatorID)) {
				//only people in wait going up
				if((peopleWaiting + ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID)) > this.capacity) {
					ElevatorScene.goingUp.get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release(spaceAvailable);
				}
				else {
					ElevatorScene.goingUp.get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release(peopleWaiting);
				}
			}
			else {
				//only people going down
				if((peopleWaiting + passangers) > capacity) {
					ElevatorScene.goingDown.get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release(spaceAvailable);
				}
				else {
					ElevatorScene.goingDown.get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release(peopleWaiting);
				}
			}
			//update passenger count
			if(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID) > 6) {
				System.out.println("Elevator " + elevatorID + " overloaded with " + ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID) + " passengers!");
			}
			//this.passangers = ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void releasePassengers() {
		try {
			//by using the combination of inElevatorMutex and exitFloors we can release only those passengers intended to exit on the elevators current floor
			ElevatorScene.inElevatorMutex.get(elevatorID).release(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
			ElevatorScene.exitFloors.get(elevatorID).get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
			//sleep to allow other threads(passengers) to process
			Thread.sleep(400);
			//Acquire the semaphores released again for each individual in the elevator
			ElevatorScene.inElevatorMutex.get(elevatorID).tryAcquire(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
			ElevatorScene.exitFloors.get(elevatorID).get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).tryAcquire(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
			//update the passenger count
			//this.passangers = ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
