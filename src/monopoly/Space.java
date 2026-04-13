package monopoly;

import java.util.Set;
import javafx.scene.paint.Color;
public abstract class Space {
	
	
	public Space nextSpace;
	public String name;
	public String description;
	public Set<Player> playersOnSpace;
	protected Color color = Color.NONE; //default to none
	public enum Color{
		NONE,
		BROWN(),
		LIGHTBLUE,
		PINK,
		ORANGE,
		RED,
		YELLOW,
		GREEN,
		BLUE
		
	}
	
	public String getName() {
		return name;
	}
	
	public Set<Player> getPlayersOnSpace(){
		return playersOnSpace;
	}
	
	public void setNextSpace(Space newSpace) {
		nextSpace = newSpace;
	}
	
	public Space getNextSpace() {
		return nextSpace;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	
    public javafx.scene.paint.Color getFXColor() {
    	
        switch (color) {
            case BLUE:
                return javafx.scene.paint.Color.BLUE;
            case BROWN:
                return javafx.scene.paint.Color.BROWN;
            case LIGHTBLUE:
                return javafx.scene.paint.Color.LIGHTBLUE;
            case PINK:
                return javafx.scene.paint.Color.PINK;
            case ORANGE:
                return javafx.scene.paint.Color.ORANGE;
            case GREEN:
                return javafx.scene.paint.Color.GREEN;
            case RED:
                return javafx.scene.paint.Color.RED;
            case YELLOW:
                return javafx.scene.paint.Color.YELLOW;
			default:
				return javafx.scene.paint.Color.LIGHTGREEN;
        }
        
    }
}