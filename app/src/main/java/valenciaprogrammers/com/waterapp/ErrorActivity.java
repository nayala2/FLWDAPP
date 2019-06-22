package valenciaprogrammers.com.waterapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
    }

    public void backToStartActivity(View view) {
        Intent intent = new Intent(ErrorActivity.this, StartActivity.class);
        startActivity(intent);
    }
}
