package monopoly;

import java.util.List;

import monopoly.Space.Color;

public abstract class Space {
	
	
	public Space nextSpace;
	public String name;
	public String description;
	public List<Player> playersOnSpace;
	protected Color color;
	public enum Color{
		NONE,
		BROWN,
		LIGHTBLUE,
		PINK,
		ORANGE,
		RED,
		YELLOW,
		GREEN,
		BLUE
		
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
				return javafx.scene.paint.Color.WHITE;
        }
        
    }
}