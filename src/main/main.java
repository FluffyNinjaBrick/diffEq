package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.function.Function;

public class main extends Application {

    Stage window;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception{

        /////CALCULATIONS
        int steps = 10;
        //generate base function list
        ArrayList<Function<Double, Double>> functionBase = functions.baseFunctions(steps);

        //generate bilinear matrix
        ArrayList<Double> bilinearValuesList = new ArrayList<>();
        for (int i = 0; i < steps; i++) {
            for (int j = 0; j < steps; j++) {
                bilinearValuesList.add(functions.bilinearOf(functionBase.get(i), functionBase.get(j)));
            }
        }
        double[] bilinearValuesArray = new double[bilinearValuesList.size()];
        for (int i = 0; i < bilinearValuesList.size(); i++) bilinearValuesArray[i] = bilinearValuesList.get(i);
        SimpleMatrix B = new SimpleMatrix(steps, steps,true, bilinearValuesArray);

        //generate linear matrix
        ArrayList<Double> linearValuesList = new ArrayList<>();
        for (int i = 0; i < steps; i++) {
            linearValuesList.add(functions.linearOf(functionBase.get(i)));
        }
        double[] linearValuesArray = new double[linearValuesList.size()];
        for (int i = 0; i < linearValuesList.size(); i++) linearValuesArray[i] = linearValuesList.get(i);
        SimpleMatrix L = new SimpleMatrix(steps, 1,false, linearValuesArray);

        //get multiplier matrix as U = B^-1 * L
        B = B.invert();
        SimpleMatrix U = B.mult(L);


        //create function for plotting
        Function<Double,Double> heatFlow = (x) -> {
          Double value = 0.0;
          for (int i = 0; i < steps; i++) {
              value += U.get(i, 0) * functionBase.get(i).apply(x);
          }
          return value;
        };

        /////VISUALIZATION
        window = primaryStage;
        window.setTitle("Heat flow equation solver");
        window.setMinWidth(500);
        window.setMinHeight(500);

        HBox layout = new HBox();

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("x");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("u(x)");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Heat Flow");

        XYChart.Series<Number, Number> data = new XYChart.Series<>();
        for (int i = 0; i <= 100; i++) {
            double x = i * 0.02;
            data.getData().add(new XYChart.Data<Number, Number>(x, heatFlow.apply(x)));
        }
        lineChart.getData().add(data);
        layout.getChildren().add(lineChart);

        Scene diagramScene = new Scene(layout, 500, 500);
        window.setScene(diagramScene);
        window.show();

    }
}
