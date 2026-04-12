package monopoly;

import java.util.List;

public abstract class Space {
	
	
	public Space nextSpace;
	public String name;
	public String description;
	public List<Player> playersOnSpace;
	protected Color color;
	public enum Color{
		NONE,
		BLUE,
		RED,
		ORANGE,
		GREEN,
		LIGHTGREEN,
		PURPLE
		
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
            case GREEN:
                return javafx.scene.paint.Color.GREEN;
			default:
				return javafx.scene.paint.Color.WHITE;
        }
	
    }
}