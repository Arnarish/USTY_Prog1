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
				//System.out.println("Elevator " + elevatorID + " Opening door at floor " + ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID));
				//release semaphores so passengers can exit the elevator
				ElevatorScene.inElevatorMutex.get(elevatorID).release(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
				//gives other threads time to work
				Thread.sleep(500);
				//block so people can't exit the elevator if current floor is not the destination
				ElevatorScene.inElevatorMutex.get(elevatorID).tryAcquire(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
				
				ElevatorScene.exitFloors.get(elevatorID).get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
				Thread.sleep(500);
				ElevatorScene.exitFloors.get(elevatorID).get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).tryAcquire(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
				
				//load passengers
				this.passangers = ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID);
				System.out.println(this.passangers);
				if((capacity - passangers) > 0) {
					if(ElevatorScene.eScene.elevatorGoingUp.get(elevatorID)) {
						//only people in wait going up
						int peopleWaiting = ElevatorScene.eScene.getNumberOfPeopleWaitingAtFloor(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID));
						if(peopleWaiting + passangers > capacity) {
							ElevatorScene.goingUp.get(elevatorID).release((capacity-passangers));
						}
						else {
							ElevatorScene.goingUp.get(elevatorID).release(peopleWaiting);
						}
					}
					else {
						int peopleWaiting = ElevatorScene.eScene.getNumberOfPeopleWaitingAtFloor(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID));
						if((peopleWaiting + passangers) > capacity) {
							ElevatorScene.goingDown.get(elevatorID).release(capacity-passangers);
						}
						else {
							ElevatorScene.goingDown.get(elevatorID).release(peopleWaiting);
						}
					}
				}
				// close the door, and move on to the next floor
				ElevatorScene.elevatorOpenMutex.release();
				ElevatorScene.eScene.nextFloor(elevatorID);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
