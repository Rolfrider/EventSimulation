package com.Dp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application{
    private Stage window;
    private Simulator sim ;
    private TextField durationText;
    private CheckBox adaptiveCheck;
    public static void main(String[] args) {
//	    Simulator sim = new Simulator();
//	    sim.startSimulation(600, true);
//	    //sim.startSimulation(300, true);
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Simulation starter");


        Button startB = new Button("Start");
        startB.setOnAction( e -> simuulate());
        Button endB = new Button("Exit");
        endB.setOnAction( e -> window.close());

        durationText = new TextField();
        durationText.setPromptText("Duration time");

        Label label = new Label("Time of simulation");

        adaptiveCheck = new CheckBox("Adaptive");


        VBox layout = new VBox(10);
        layout.getChildren().addAll(label,durationText,adaptiveCheck,startB, endB);
        layout.setPadding(new Insets(10,10,10,10));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 150, 150);
        window.setScene(scene);
        window.show();
    }

    private int getTime(){
        int value ;
        try {
             value= Integer.parseInt(durationText.getText());
        }catch (NumberFormatException e){
            value = -1;
        }
        return value;
    }

    private void simuulate(){
        int time = getTime();

        if(time > 0) {
            sim = new Simulator();
            sim.startSimulation(time, adaptiveCheck.isSelected());
        }
    }


}
