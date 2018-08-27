package com.notificaciones.uteq;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText nombreusuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void Enviar(View view){
        Intent intent= new Intent(MainActivity.this, Geo.class);
        nombreusuario= (EditText)findViewById(R.id.etnombreuser);
        Bundle b = new Bundle();
        b.putString("NOMBRE", nombreusuario.getText().toString());
        intent.putExtras(b);
        startActivity(intent);
    }
}
