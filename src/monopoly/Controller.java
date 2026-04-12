package monopoly;

import java.util.List;

public class Controller {

	private Model model;
	
	
	public Controller() {
		model = new Model();
	}
	

	
	
	
	public int getTotalSpaces() {
		return model.board.getTotalSpaces();
	}
	
	public List<Space> getSpaces(){
		return model.board.getSpaces();
	}
	
	
	public int getBoardWidth() {
		return model.board.getBoardWidth();
	}
	
	
}
