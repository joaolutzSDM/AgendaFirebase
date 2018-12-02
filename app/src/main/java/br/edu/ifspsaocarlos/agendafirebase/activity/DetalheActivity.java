package br.edu.ifspsaocarlos.agendafirebase.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifspsaocarlos.agendafirebase.model.Contato;
import br.edu.ifspsaocarlos.agendafirebase.R;


public class DetalheActivity extends AppCompatActivity {
    private Contato c;
    private Spinner spinner;
    private DatabaseReference databaseReference;
    private List<String> categorias = new ArrayList<>();
    String FirebaseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinner = findViewById(R.id.spinnerTipoContato);

        categorias.add("Amigo");
        categorias.add("Família");
        categorias.add("Trabalho");
        categorias.add("Outro");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categorias);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (getIntent().hasExtra("FirebaseID")) {

            FirebaseID=getIntent().getStringExtra("FirebaseID");

              databaseReference.child(FirebaseID).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    c = snapshot.getValue(Contato.class);

                    if (c != null) {
                        EditText nameText = findViewById(R.id.editTextNome);
                        nameText.setText(c.getNome());

                        EditText foneText = findViewById(R.id.editTextFone);
                        foneText.setText(c.getFone());

                        EditText emailText = findViewById(R.id.editTextEmail);
                        emailText.setText(c.getEmail());

                        spinner.setSelection(categorias.indexOf(c.getTipoContato()));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detalhe, menu);
        if (FirebaseID==null)
        {
            MenuItem item = menu.findItem(R.id.delContato);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.salvarContato:
                salvar();
                return true;
            case R.id.delContato:
                apagar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void apagar()
    {
        databaseReference.child(FirebaseID).removeValue();
        Intent resultIntent = new Intent();
        setResult(3,resultIntent);
        finish();
    }

    private void salvar()
    {
        String name = ((EditText) findViewById(R.id.editTextNome)).getText().toString();
        String fone = ((EditText) findViewById(R.id.editTextFone)).getText().toString();
        String email = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
        boolean insert = c == null;

        if (insert) {
            c = new Contato();
        }

        c.setNome(name);
        c.setFone(fone);
        c.setEmail(email);
        c.setTipoContato(spinner.getSelectedItem().toString());

        if (insert) {
            databaseReference.push().setValue(c);
        } else {
            databaseReference.child(FirebaseID).setValue(c);
        }

        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}