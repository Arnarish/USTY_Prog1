package com.ru.usty.elevator;

import com.ru.usty.elevator.visualization.TestSuite;

public class ElevatorMainProgram {
	public static void main(String[] args) {

		try {

			TestSuite.startVisualization();

/***EXPERIMENT HERE BUT THIS WILL BE CHANGED DURING GRADING***/

			Thread.sleep(1000);

<<<<<<< HEAD
			TestSuite.runTest(2);
=======
			TestSuite.runTest(0);
>>>>>>> 01d5562050edcba03e63511a92c91d256e73cfab

			Thread.sleep(2000);

		//	for(int i = 0; i <= 9; i++) {
		//		TestSuite.runTest(i);
		//		Thread.sleep(2000);
		//	}

/*************************************************************/

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.exit(0);
	}
}
