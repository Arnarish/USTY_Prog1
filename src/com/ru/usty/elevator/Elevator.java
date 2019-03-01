package com.ru.usty.elevator;



public class Elevator implements Runnable{
	int elevatorID, currFloor, capacity, passangers;
	
	public Elevator(int elevatorID, int currFloor, int capacity, int passangersTotal) {
		this.elevatorID = elevatorID;
		this.currFloor = currFloor;
		this.capacity = ElevatorScene.ELEVATOR_MAX;
		this.passangers = passangersTotal;
	}
	
	
	public void run() {
		while(true) {
			try{
				//first open the doors, not allowing other elevators to open.
				ElevatorScene.elevatorOpenMutex.acquire();
				ElevatorScene.elevatorOpen = elevatorID;
				//update whether the elevator is going up or down
				if(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID) >= 0) {
					releasePassengers();
					loadPassengers();
					releasePassengers();
				}
				else {
					loadPassengers();
					releasePassengers();
				}
			
				// close the door, and move on to the next floor
				ElevatorScene.elevatorOpenMutex.release();
				ElevatorScene.eScene.nextFloor(elevatorID);
				ElevatorScene.eScene.floorTransition(elevatorID);
				
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void loadPassengers() {
		try {
			this.passangers = ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID);
				int peopleWaiting = ElevatorScene.eScene.getNumberOfPeopleWaitingAtFloor(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID));
				if(ElevatorScene.eScene.elevatorGoingUp.get(elevatorID)) {
					//only people in wait going up
					//System.out.println("Load up");
					if(peopleWaiting + passangers > capacity) {
						ElevatorScene.goingUp.get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release((capacity-passangers));
					}
					else {
						ElevatorScene.goingUp.get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release(peopleWaiting);
					}
				}
				else {
					//System.out.println("Load Down");
					if((peopleWaiting + passangers) > capacity) {
						ElevatorScene.goingDown.get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release(capacity-passangers);
					}
					else {
						ElevatorScene.goingDown.get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release(peopleWaiting);
					}
				}
				Thread.sleep(200);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void releasePassengers() {
		try {
			ElevatorScene.inElevatorMutex.get(elevatorID).release(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
			//gives other threads time to work
			Thread.sleep(400);
			//block so people can't exit the elevator if current floor is not the destination
			ElevatorScene.inElevatorMutex.get(elevatorID).tryAcquire(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
			
			ElevatorScene.exitFloors.get(elevatorID).get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
			Thread.sleep(400);
			ElevatorScene.exitFloors.get(elevatorID).get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).tryAcquire(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
