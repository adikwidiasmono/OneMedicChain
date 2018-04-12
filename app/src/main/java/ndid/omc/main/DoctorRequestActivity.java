package ndid.omc.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import ndid.omc.firebase.FirebaseDB;

public class DoctorRequestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_request);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Medical Record Access Request");
        setSupportActionBar(toolbar);

        Button btRequest = findViewById(R.id.bt_request_access);
        btRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDB.init().updateNotif();
            }
        });
    }
}
