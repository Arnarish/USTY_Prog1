package com.ru.usty.elevator;



public class Elevator implements Runnable{
	int elevatorID, currFloor, capacity, passangers;
	
	public Elevator(int elevatorID, int capacity, int passangersTotal) {
		this.elevatorID = elevatorID;
		this.capacity = ElevatorScene.ELEVATOR_MAX;
		this.passangers = passangersTotal;
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
				//release/accept passengers
				System.out.println("Elevator: " + elevatorID);
				if(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID) >= 0) {
					releasePassengers();
					Thread.sleep(200);
					loadPassengers();
					Thread.sleep(200);
					releasePassengers();
				}
				else {
					// lift is empty, we can start by loading it.
					loadPassengers();
					Thread.sleep(200);
					releasePassengers();
				}
				// close the door, and move on to the next floor
				ElevatorScene.elevatorOpenMutex.get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release();
				if(this.passangers > 0 || ElevatorScene.eScene.checkAny()) {
					ElevatorScene.eScene.nextFloor(elevatorID);
					ElevatorScene.eScene.floorTransition(elevatorID);
					Thread.sleep(400);
				}
				else {
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
			this.passangers = ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID);
			if(ElevatorScene.eScene.checkMiddle() && (ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID) == 0 || ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID) == ElevatorScene.eScene.getNumberOfFloors()-1)) {
				this.capacity = ElevatorScene.ELEVATOR_MAX_TOPBOT;
			}
			else {
				this.capacity = ElevatorScene.ELEVATOR_MAX;
			}
				int peopleWaiting = ElevatorScene.eScene.getNumberOfPeopleWaitingAtFloor(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID));
				if(ElevatorScene.eScene.elevatorGoingUp.get(elevatorID)) {
					//only people in wait going up
					if((peopleWaiting + this.passangers) > this.capacity) {
						System.out.println("Space in elevator: " + (this.capacity-this.passangers));
						ElevatorScene.goingUp.get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release(this.capacity-this.passangers);
					}
					else {
						System.out.println("Space in elevator: " + (this.capacity-this.passangers));
						ElevatorScene.goingUp.get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release(peopleWaiting);
					}
				}
				else {
					//only people going down
					if((peopleWaiting + passangers) > capacity) {
						ElevatorScene.goingDown.get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release(capacity-passangers);
					}
					else {
						ElevatorScene.goingDown.get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release(peopleWaiting);
					}
				}
				this.passangers = ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void releasePassengers() {
		try {
			//System.out.println("Floor: " + ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID) + " arriving: " + ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
			ElevatorScene.inElevatorMutex.get(elevatorID).release(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
			//gives other threads time to work
			Thread.sleep(200);
			//block so people can't exit the elevator if current floor is not the destination
			ElevatorScene.inElevatorMutex.get(elevatorID).tryAcquire(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
			
			ElevatorScene.exitFloors.get(elevatorID).get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).release(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
			Thread.sleep(200);
			ElevatorScene.exitFloors.get(elevatorID).get(ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID)).tryAcquire(ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
			this.passangers = ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID);
			//System.out.println("Floor: " + ElevatorScene.eScene.getCurrentFloorForElevator(elevatorID) + " departing: " + ElevatorScene.eScene.getNumberOfPeopleInElevator(elevatorID));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
