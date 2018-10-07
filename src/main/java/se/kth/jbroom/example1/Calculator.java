package se.kth.jbroom.example1;

public class Calculator {

    public double add(double number1, double number2) {
        return number1 + number2;
    }

    public double substract(double number1, double number2) {
        return number1 - number2;
    }

    public double multiply(double number1, double number2) {
        return subMethod(number1) * subMethod(number2);
    }

    public double divide(double number1, double number2) {
        return number1 / number2;
    }

    public double divide2(double number1, double number2) {
        return number1 / number2;
    }

    public double subMethod(double a) {
        return a / 2;
    }


    public int sumElements(Element element1, Element element2){
        return element1.getElement() + element2.getElement();

    }

}
