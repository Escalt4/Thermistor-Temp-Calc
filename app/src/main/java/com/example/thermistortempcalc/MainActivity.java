package com.example.thermistortempcalc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    SharedPreferences settings;
    SharedPreferences.Editor prefEditor;

    String[] units = {"Ω", "kΩ", "mΩ"};
    Integer[] multipliers = {1, 1000, 1000000};

    EditText editText_Beta;
    EditText editText_R25C;
    EditText editText_RC;
    TextView textView;
    Button button_R25C;
    Button button_RC;

    Integer Beta;
    float R25C;
    float RC;
    float Temp;
    Integer R25C_multiplier;
    Integer RC_multiplier;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_Beta = findViewById(R.id.editText_Beta);
        editText_R25C = findViewById(R.id.editText_R25С);
        editText_RC = findViewById(R.id.editText_RC);
        textView = findViewById(R.id.textView);
        button_R25C = findViewById(R.id.button_R25C);
        button_RC = findViewById(R.id.button_RC);

        settings = getSharedPreferences("Settings", MODE_PRIVATE);
        prefEditor = settings.edit();

        load_settings_if_exist();
    }

    String del_end_point_zero(String str) {
        if (str.endsWith(".0")) {
            str = str.replace(".0", "");
        }
        return str;
    }

    public void load_settings_if_exist() {
        if (settings.contains("Beta")) {
            Beta = settings.getInt("Beta", 0);
            editText_Beta.setText(Integer.toString(Beta));
        }
        if (settings.contains("R25C")) {
            R25C = settings.getFloat("R25C", 0);
            editText_R25C.setText(del_end_point_zero(Float.toString(R25C)));
        }
        if (settings.contains("RC")) {
            RC = settings.getFloat("RC", 0);
            editText_RC.setText(del_end_point_zero(Float.toString(RC)));
        }
        if (settings.contains("Temp")) {
            Temp = settings.getFloat("Temp", 0);
            textView.setText(String.format("%.2f", Temp).replace(',', '.') + " C°");
        }
        R25C_multiplier = settings.getInt("R25C_multiplier", 1);
        button_R25C.setText(units[Arrays.asList(multipliers).indexOf(R25C_multiplier)]);
        RC_multiplier = settings.getInt("RC_multiplier", 1);
        button_RC.setText(units[Arrays.asList(multipliers).indexOf(RC_multiplier)]);
    }

    public void save_settings() {
        prefEditor.putInt("Beta", Beta);
        prefEditor.putFloat("R25C", R25C);
        prefEditor.putFloat("RC", RC);
        prefEditor.putFloat("Temp", Temp);
        prefEditor.putInt("R25C_multiplier", R25C_multiplier);
        prefEditor.putInt("RC_multiplier", RC_multiplier);
        prefEditor.apply();
    }

    public void showDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(units, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (view.getId() == R.id.button_R25C) {
                    R25C_multiplier = multipliers[which];
                    button_R25C.setText(units[which]);
                }
                if (view.getId() == R.id.button_RC) {
                    RC_multiplier = multipliers[which];
                    button_RC.setText(units[which]);
                }
                Calculate();
            }
        }).create().show();
    }

    public void Calculate(View view) {
        Calculate();
    }

    public void Calculate() {
        if (editText_Beta.getText().toString().equals("") || editText_R25C.getText().toString().equals("") || editText_RC.getText().toString().equals("")) {
            return;
        }

        Beta = Integer.parseInt(editText_Beta.getText().toString());
        R25C = Float.parseFloat(editText_R25C.getText().toString());
        RC = Float.parseFloat(editText_RC.getText().toString());

        if (Beta == 0 || R25C == 0 || RC == 0) {
            return;
        }

        Temp = (float) (1 / (1 / (25 + 273.15) + Math.log((RC * RC_multiplier) / (R25C * R25C_multiplier)) / Math.log(2.718281828459) / Beta) - 273.15);

        textView.setText(String.format("%.2f", Temp).replace(',', '.') + " C°");

        save_settings();
    }
}