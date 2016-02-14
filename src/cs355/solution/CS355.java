package cs355.solution;

import java.awt.Color;
import java.awt.MouseInfo;

import cs355.GUIFunctions;
import cs355.controller.Controller;
import cs355.model.drawing.Model;
import cs355.view.View;

/**
 * This is the main class. The program starts here.
 * Make you add code below to initialize your model,
 * view, and controller and give them to the app.
 */
public class CS355 {

	/**
	 * This is where it starts.
	 * @param args = the command line arguments
	 */
	public static void main(String[] args) 
	{
		//System.out.println("Starting Program...");
		Controller the_controller = new Controller();
		View the_view = new View();
		
		Model.instance().addObserver(the_view);
		Model.instance().setSelectedColor(Color.GREEN);
		
		GUIFunctions.createCS355Frame(the_controller, the_view);
		GUIFunctions.changeSelectedColor(Color.GREEN);
		GUIFunctions.refresh();	
		
	}
}
