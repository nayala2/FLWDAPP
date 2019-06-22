package valenciaprogrammers.com.waterapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DateErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_error);
    }

    public void backToDateRangeActivity(View view) {
        Intent intent = new Intent(DateErrorActivity.this, DateRangeActivity.class);
        startActivity(intent);
    }
}
