package muryshkin.alexey.irbiz.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import muryshkin.alexey.irbiz.Data.DataHolder;
import muryshkin.alexey.irbiz.R;

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "ResultActivity";
    private static final int PICK_IMAGE = 1;

    private static final String SHARED_PREFS_FILE_NAME = "muryshkin.alexey.irbiz.sharedprefs";
    private static final String SHARED_PREFS_BESTSCORE_KEY = "bestscore";
    private SharedPreferences sharedPreferences;

    private ProgressBar myProgressBar;

    private TextView scoreTextView;
    private TextView bestScoreTextView;
    private ImageButton mainActivityButton;
    private ImageButton restartButton;
    private ImageButton shareButton;

    private ImageView medalImageView;
    private TextView congratulationsTextView;
    private RelativeLayout resultRelativeLayout;

    private ImageView profileImageView;
    private TextView changeImageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        myProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        myProgressBar.setVisibility(View.INVISIBLE);
        myProgressBar.setEnabled(false);

        sharedPreferences = getSharedPreferences(SHARED_PREFS_FILE_NAME, MODE_PRIVATE);

        float result = DataHolder.getDataHolder().result;
        float total = DataHolder.getDataHolder().generatedList.size();

        resultRelativeLayout = (RelativeLayout) findViewById(R.id.resultRelativeLayout);

        congratulationsTextView = (TextView) findViewById(R.id.congratulationsTextView);

        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        scoreTextView.setText(new DecimalFormat("###.#").format(result / total * 100) + "%");

        bestScoreTextView = (TextView) findViewById(R.id.bestScoreTextView);
        if (sharedPreferences.getFloat(SHARED_PREFS_BESTSCORE_KEY, 0) - (result / total * 100) > 0.01) {
            bestScoreTextView.setText(new DecimalFormat("###.#").format(sharedPreferences.getFloat(SHARED_PREFS_BESTSCORE_KEY, 0)) + "%");
            congratulationsTextView.setText("Я знаю нашу олимпийскую сборную на " + new DecimalFormat("###.#").format(sharedPreferences.getFloat(SHARED_PREFS_BESTSCORE_KEY, 0)) + "%!");
        }
        else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat(SHARED_PREFS_BESTSCORE_KEY, result / total * 100);
            editor.commit();
            bestScoreTextView.setText(new DecimalFormat("###.#").format(result / total * 100) + "%");
            congratulationsTextView.setText("Я знаю нашу олимпийскую сборную на " + new DecimalFormat("###.#").format(result / total * 100) + "%!");
        }

        medalImageView = (ImageView) findViewById(R.id.medalImageView);

        if (Math.floor(sharedPreferences.getFloat(SHARED_PREFS_BESTSCORE_KEY, 0)) >= 80)
            Glide.with(this).load(getResources().getIdentifier("@drawable/gold", null, getPackageName())).into(medalImageView);
        else if (Math.floor(sharedPreferences.getFloat(SHARED_PREFS_BESTSCORE_KEY, 0)) >= 60)
            Glide.with(this).load(getResources().getIdentifier("@drawable/silver", null, getPackageName())).into(medalImageView);
        else if (Math.floor(sharedPreferences.getFloat(SHARED_PREFS_BESTSCORE_KEY, 0)) >= 40)
            Glide.with(this).load(getResources().getIdentifier("@drawable/bronze", null, getPackageName())).into(medalImageView);
        else {
            float scale = getResources().getDisplayMetrics().density;
            int leftPadding = (int) (35*scale + 0.5f);
            int rightPadding = (int) (35*scale + 0.5f);
            int topPadding = (int) (5*scale + 0.5f);
            congratulationsTextView.setPadding(leftPadding, topPadding, rightPadding, 0);
        }

        mainActivityButton = (ImageButton) findViewById(R.id.homeImageButton);
        mainActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMainClick();
            }
        });

        restartButton = (ImageButton) findViewById(R.id.restartImageButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRestartClick();
            }
        });

        shareButton = (ImageButton) findViewById(R.id.shareImageButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareClick();
            }
        });

        profileImageView = (ImageView) findViewById(R.id.profileImageView);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeImageClick();
            }
        });

        changeImageTextView = (TextView) findViewById(R.id.changeImageTextView);
        changeImageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeImageClick();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainActivityButton.setEnabled(true);
        restartButton.setEnabled(true);
        shareButton.setEnabled(true);
        changeImageTextView.setEnabled(true);
        profileImageView.setEnabled(true);
    }

    private void onChangeImageClick() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Выберите картинку");
        //chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {

                Bitmap bitmap = null;

                if (data != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    profileImageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    private void onShareClick() {
        mainActivityButton.setEnabled(false);
        restartButton.setEnabled(false);
        shareButton.setEnabled(false);
        changeImageTextView.setEnabled(false);
        profileImageView.setEnabled(false);

        resultRelativeLayout.setDrawingCacheEnabled(true);
        //resultRelativeLayout.buildDrawingCache(true);
        Bitmap bitmap = resultRelativeLayout.getDrawingCache();
        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = null; // overwrites this image every time
            stream = new FileOutputStream(cachePath + "/image.png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File imagePath = new File(getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(this, "muryshkin.alexey.irbiz.fileprovider", newFile);

        if (contentUri != null) {

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, "Поделиться в"));
        }
    }

    private void onRestartClick() {
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
        finish();
    }

    private void onMainClick() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
