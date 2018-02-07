package muryshkin.alexey.irbiz.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import muryshkin.alexey.irbiz.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RelativeLayout playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = (RelativeLayout) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlayClick();
            }
        });
    }

    private void onPlayClick() {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
    }
}
