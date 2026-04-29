package Cards;

import java.io.Serializable;

import Monopoly.Model;
import Spaces.Player;

public interface CardLambdaInterface {
	public void run (Player player, Model model);
}
