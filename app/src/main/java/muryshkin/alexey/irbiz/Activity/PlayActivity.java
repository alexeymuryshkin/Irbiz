package muryshkin.alexey.irbiz.Activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import muryshkin.alexey.irbiz.Data.DataHolder;
import muryshkin.alexey.irbiz.ExtendedObjects.NonSwipeableViewPager;
import muryshkin.alexey.irbiz.Fragment.QuestionFragment;
import muryshkin.alexey.irbiz.Adapter.QuestionsAdapter;
import muryshkin.alexey.irbiz.R;

public class PlayActivity extends AppCompatActivity {

    private static final String TAG = "PlayActivity";

    private NonSwipeableViewPager viewPager;
    private TextView timerTextView;
    private TextView scoreTextView;
    private TextView questionNumberTextView;

    private List<Pair<Integer, Integer>> a;

    private CountDownTimer countDownTimer;

    private ProgressBar myProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_play);

        viewPager = (NonSwipeableViewPager) findViewById(R.id.questionsViewPager);
        timerTextView = (TextView) findViewById(R.id.timerTextView);

        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        scoreTextView.setText("0");

        myProgressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        myProgressBar.setVisibility(View.VISIBLE);

        if (DataHolder.getDataHolder().participants.size() == 0)
            getTest();

        myProgressBar.setVisibility(View.INVISIBLE);

        try {
            displayInfo();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        questionNumberTextView = (TextView) findViewById(R.id.questionNumberTextView);
        questionNumberTextView.setText("1/" + DataHolder.getDataHolder().generatedList.size());

        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                String sec = "";
                if (millisUntilFinished / 1000 % 60 < 10)
                    sec += "0";
                timerTextView.setText("0" + (millisUntilFinished / 60000) + ":" + sec + (millisUntilFinished / 1000 % 60));
            }

            public void onFinish() {
                showResult();
            }
        };

        countDownTimer.start();
    }

    private void getTest() {

        String json;

        AssetManager assetManager = getAssets();

        try {
            InputStream inputStream = assetManager.open("test.json", MODE_PRIVATE);
            int size = inputStream.available();

            byte[] buffer = new byte[size];
            inputStream.read(buffer);

            inputStream.close();

            json = new String(buffer, "UTF-8");
            JSONArray data = new JSONArray(json);

            requestHelper(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestHelper(JSONArray data) {

        for (int i = 0; i < data.length(); i++)
            try {
                DataHolder.getDataHolder().participants.add(data.getJSONObject(i));

                if (data.getJSONObject(i).getString("gender").equals("male"))
                    DataHolder.getDataHolder().males.add(data.getJSONObject(i));
                else if (data.getJSONObject(i).getString("gender").equals("female"))
                    DataHolder.getDataHolder().females.add(data.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    private void displayInfo() throws JSONException {
        ArrayList<Fragment> fragments = new ArrayList<>();
        DataHolder.getDataHolder().answers = new LinkedList<>();
        DataHolder.getDataHolder().correctAnswers = new LinkedList<>();
        DataHolder.getDataHolder().result = 0;
        DataHolder.getDataHolder().answered = 0;

        a = new ArrayList<>();
        Random rn = new Random();

        for (int i = 0; i < DataHolder.getDataHolder().participants.size(); i++)
            a.add(Pair.create(i, rn.nextInt(100000000)));

        qsort(0, a.size() - 1);
        DataHolder.getDataHolder().generatedList = new ArrayList<>();
        int nParticipantsInTest = 30;

        for (int i = 0; i < nParticipantsInTest; i++) {
            DataHolder.getDataHolder().generatedList.add(a.get(i).first);
        }

        for (int i = 0; i < nParticipantsInTest; i++) {
            DataHolder.getDataHolder().answers.add(100);
            DataHolder.getDataHolder().correctAnswers.add(100);
            fragments.add(QuestionFragment.newInstance(i));
        }

        QuestionsAdapter adapter = new QuestionsAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
    }

    private void qsort(int l, int r) {

        int i = l;
        int j = r;
        int s = a.get((l + r) / 2).second;

        while (i <= j) {
            while (a.get(i).second < s) i++;
            while (a.get(j).second > s) j--;

            if (i <= j) {
                Pair<Integer, Integer> d = a.get(i);
                a.set(i, a.get(j));
                a.set(j, d);

                i++; j--;
            }
        }

        if (i < r) qsort(i, r);
        if (l < j) qsort(l, j);
    }

    public void setCurrentItem (final int item, final boolean smoothScroll) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        if (item >= DataHolder.getDataHolder().generatedList.size())
                            showResult();

                        if (item < DataHolder.getDataHolder().generatedList.size()) {
                            questionNumberTextView.setText((item + 1) + "/" + DataHolder.getDataHolder().generatedList.size());
                            viewPager.setCurrentItem(item, smoothScroll);
                            String str = "";
                            if (item < 9)
                                str += "00";
                            else if (item < 99)
                                str += "0";

                            scoreTextView.setText("" + DataHolder.getDataHolder().result);

                            if (DataHolder.getDataHolder().answers.get(item - 1) != 100 && DataHolder.getDataHolder().correctAnswers.get(item - 1) == DataHolder.getDataHolder().answers.get(item - 1))
                                scoreTextView.setTextColor(Color.parseColor("#76FF03"));
                            else
                                scoreTextView.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                    }
                });
            }
        }, 500);
    }

    private void showResult() {
        Intent intent = new Intent(this, ResultActivity.class);
        startActivity(intent);
        if(countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        if(countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        finish();
        return;
    }
}
