package main;

import java.util.ArrayList;
import java.util.function.Function;

public class functions {

    //counts integral of function from bottomLimit to topLimit using rectangle method
    static Double signedIntegralOf(Function<Double, Double> function, Double topLimit, Double bottomLimit) {
        Double value = 0.0;
        int stepAmount = 10000;
        Double step = (topLimit - bottomLimit) / stepAmount;
        for (int i = 1; i < stepAmount; i++) {
            value += function.apply(bottomLimit + i*step) * step;
        }
        return value;
    }

    static Function<Double, Double> derivativeOf(Function<Double, Double> function) {
        Double diff = 0.0000000001;
        return (x) -> (function.apply(x+diff) - function.apply(x)) / diff;
    }

    static Double bilinearOf(
            Function<Double, Double> u,
            Function<Double, Double> v) {
        Function<Double, Double> uDerivative = derivativeOf(u);
        Function<Double, Double> vDerivative = derivativeOf(v);

        Function<Double, Double> forIntegration = (x) -> derivativeOf(u).apply(x) * derivativeOf(v).apply(x);

        return (u.apply(0.0) * v.apply(0.0))
                - (uDerivative.apply(1.0) * vDerivative.apply(1.0))
                - signedIntegralOf(forIntegration, 1.0, 0.0)
                - 2 * signedIntegralOf(forIntegration, 2.0, 1.0);
    }

    static Double linearOf(Function<Double, Double> v) {
        return 20 * v.apply(0.0);
    }

    static Function<Double, Double> ithBaseFunction(Double step, int i) {
        if (i == 0) {
            return (x) -> {
                if (x < step) return 1.0 - x/step;
                else return 0.0;
            };
        }
        else return (x) -> {
            if (x <= i*step && x > (i-1)*step) return (x/step - (i-1));
            else if (x >= i*step && x < (i+1)*step) return ((i+1) - x/step);
            else return 0.0;
        };
    }

    static ArrayList<Function<Double, Double>> baseFunctions(int steps) {
        double stepValue = 2.0/steps;
        ArrayList<Function<Double, Double>> base = new ArrayList<>();
        for (int i = 0; i < steps; i++) base.add(ithBaseFunction(stepValue, i));
        return base;
    }
}

