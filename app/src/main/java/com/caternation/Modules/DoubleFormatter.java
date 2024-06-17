package com.caternation.Modules;

import android.widget.EditText;

import java.util.regex.Pattern;

public class DoubleFormatter {
    public static String currencyFormat(double dbl){
        String outputString = String.format("%.2f", dbl);
        return outputString;
    }

    public static String realFormat(double dbl){
        // check if double number has decimal values (remainder)
        double remainder = dbl % 1;
        String outputString;

        if (remainder == 0){
            outputString = String.format("%.0f", dbl);
        }
        else{
            outputString = String.format("%.2f", dbl);

            // start - check for a trailing zero
            String lastChar = outputString.substring(outputString.length()-1);

            if (lastChar.equals("0")) {
                outputString = String.format("%.1f", dbl);
            }
            // end - check for a trailing zero
        }

        return outputString;
    }

    public static void decimalPlaceValueLimiter(EditText editText){
        String text = editText.getText().toString().trim();
        if (text.length() > 0){
            String firstStr = text.substring(0, 1);
            if (firstStr.equals(".")){
                editText.setText("0"+text);
                editText.setSelection(editText.getText().length());
            }
            if ((firstStr.equals("0")) && (text.length() > 1)){
                String secondStr = text.substring(1, 2);
                if (!secondStr.equals(".")){
                    editText.setText(secondStr);
                    editText.setSelection(editText.getText().length());
                }
            }
        }
        // check for decimal point and at least 1 decimal value
        if (text.contains(".") && !(text.endsWith("."))){
            // split by the decimal point
            String[] splitString = text.split(Pattern.quote("."));

            // count decimal places
            int decimalPlaces = splitString[1].length();

            // limit decimal places to 2 if it exceeds
            if (decimalPlaces > 2){
                editText.setText(text.substring(0, text.length() - 1));
                editText.setSelection(editText.getText().length());
            }
        }
    }
}