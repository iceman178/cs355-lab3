package cs355.controller;

import java.awt.event.MouseEvent;

public class ControllerNothingState implements IControllerState 
{
	
	@Override
	public void mousePressed(MouseEvent e) 
	{
		
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		
	}

	@Override
	public void mouseDragged(MouseEvent e) 
	{
		
	}

	@Override
	public stateType getType() 
	{
		return stateType.NOTHING;
	}

}