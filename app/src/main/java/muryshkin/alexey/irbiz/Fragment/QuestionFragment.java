package muryshkin.alexey.irbiz.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import muryshkin.alexey.irbiz.Activity.PlayActivity;
import muryshkin.alexey.irbiz.Adapter.AnswersListViewAdapter;
import muryshkin.alexey.irbiz.Data.DataHolder;
import muryshkin.alexey.irbiz.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionFragment extends Fragment {

    private static final String ARG_PARAM1 = "questionPosition";

    private int questionPosition;
    private AnswersListViewAdapter adapter;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public static QuestionFragment newInstance(int param1) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionPosition = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_question, container, false);

        ImageView questionImageView = (ImageView) v.findViewById(R.id.questionImageView);
        ListView answersListView = (ListView) v.findViewById(R.id.answersListView);

        try {
            final JSONObject participant = DataHolder.getDataHolder().participants.get(DataHolder.getDataHolder().generatedList.get(questionPosition));

            Glide.with(getContext())
                    .load(getResources().getIdentifier("@drawable/s" + DataHolder.getDataHolder().generatedList.get(questionPosition), null, getContext().getPackageName()))
                    .into(questionImageView);

            answersListView.setAdapter(displayAnswers(participant));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        answersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (DataHolder.getDataHolder().answers.get(questionPosition) == 100) {
                    DataHolder.getDataHolder().answered++;
                    DataHolder.getDataHolder().answers.set(questionPosition, i);

                    if (i == DataHolder.getDataHolder().correctAnswers.get(questionPosition))
                        DataHolder.getDataHolder().result++;

                    ((PlayActivity)getActivity()).setCurrentItem(questionPosition + 1, true);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        return v;
    }

    private AnswersListViewAdapter displayAnswers(JSONObject participant) throws JSONException {

        List<String> answers = new LinkedList<>();
        int position;

        if (DataHolder.getDataHolder().correctAnswers.get(questionPosition) == 100) {
            Random rn = new Random();
            position = rn.nextInt(4);
            DataHolder.getDataHolder().correctAnswers.set(questionPosition, position);
        } else
            position = DataHolder.getDataHolder().correctAnswers.get(questionPosition);

        for (int i = 0; i < 3; i++) {
            if (i == position)
                answers.add(participant.getString("name"));
            answers.add(generate(participant, answers));
        }

        if (position == 3)
            answers.add(participant.getString("name"));

        adapter = new AnswersListViewAdapter(getContext(), answers, questionPosition);
        return adapter;
    }

    private String generate(JSONObject participant, List<String> answers) throws JSONException {

        Random rn = new Random();
        JSONObject gen = null;
        boolean check = true;

        while (check) {
            if (participant.getString("gender").equals("male"))
                gen = DataHolder.getDataHolder().males.get(rn.nextInt(DataHolder.getDataHolder().males.size()));
            else
                gen = DataHolder.getDataHolder().females.get(rn.nextInt(DataHolder.getDataHolder().females.size()));

            check = false;

            for (int i = 0; i < answers.size(); i++)
                if (answers.get(i).equals(gen.getString("name"))) {
                    check = true;
                    break;
                }

            if (participant.equals(gen))
                check = true;
        }

        return gen.getString("name");
    }
}
