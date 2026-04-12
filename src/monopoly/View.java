package monopoly;

import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;



	
//pulling this test javafx code from lab1
public class View extends Application {
		
		private Controller controller;

		@Override
		public void start(Stage stage) throws Exception {
			
			controller = new Controller();
			
			BorderPane pane = new BorderPane();
			
			//label
			Label label = new Label("MONOPOLY");
	        label.setStyle("-fx-font-size: 30px; -fx-text-fill: darkslateblue; -fx-font-weight: bold;");
			pane.setTop(label);
			BorderPane.setAlignment(label, Pos.CENTER);
			
			//board
			GridPane gridPane = buildGridPane();			
			pane.setCenter(gridPane);		
			
			Scene scene = new Scene(pane, 800, 800);
			stage.setScene(scene);
			stage.setTitle("MONOPOLY");
			stage.show();
			
		}
		
		
		public GridPane buildGridPane() {
			
			GridPane gridPane = new GridPane();
			
			int boardDimension = controller.getBoardWidth();
		    List<Space> spaces = controller.getSpaces();
		    
			gridPane.setAlignment(Pos.CENTER);
			gridPane.setPadding(new Insets(8, 8, 8, 8));
			
			int spaceIndex = 0;
			
			
			// BOTTOM RIGHT CORNER -> BOTTOM LEFT CORNER
		    for (int col = boardDimension - 1; col >= 0; col--) {
		    	addSpaceToGrid(gridPane, spaces.get(spaceIndex++), col, boardDimension - 1);
		    }

		    // BOTTOM LEFT (one space above corner) TO TOP LEFT CORNER
		    for (int row = boardDimension - 2; row >= 0; row--) {
		    	addSpaceToGrid(gridPane, spaces.get(spaceIndex++), 0, row);
		    }

		    // TOP LEFT (one space right of corner) TO TOP RIGHT CORNER
		    for (int col = 1; col < boardDimension; col++) {
		    	addSpaceToGrid(gridPane, spaces.get(spaceIndex++), col, 0);
		    }

		    // TOP RIGHT (one space below corner) TO BOTTOM RIGHT (one space above corner)
		    for (int row = 1; row < boardDimension - 1; row++) {
		    	addSpaceToGrid(gridPane, spaces.get(spaceIndex++), boardDimension - 1, row);
		    }
			
			return gridPane;
			
			
		}
		
		
		private void addSpaceToGrid(GridPane gridPane, Space space, int col, int row) {
		    Circle circle = new Circle(20);
		    circle.setFill(space.getFXColor());
		    gridPane.add(circle, col, row);
		}
				
		
		
		

		public static void main(String[] args) {
			launch(args);
		}
	
	
}
