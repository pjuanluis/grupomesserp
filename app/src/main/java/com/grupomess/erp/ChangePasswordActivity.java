package com.grupomess.erp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etCurrent, etNew, etConfirm;
    private Button btnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etCurrent = findViewById(R.id.et_current_password);
        etNew = findViewById(R.id.et_new_password);
        etConfirm = findViewById(R.id.et_confirm_password);
        btnChange = findViewById(R.id.btn_change_password);

        btnChange.setOnClickListener(v -> {
            String current = etCurrent.getText().toString();
            String nuevo = etNew.getText().toString();
            String confirm = etConfirm.getText().toString();

            if (current.isEmpty() || nuevo.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (confirm.equals(nuevo)) {
                Toast.makeText(this, "Contraseña cambiada", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
