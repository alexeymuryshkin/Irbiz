package muryshkin.alexey.irbiz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import muryshkin.alexey.irbiz.Data.DataHolder;
import muryshkin.alexey.irbiz.R;

/**
 * Created by 123 on 8/6/2016.
 */
public class AnswersListViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<String> answers;
    private int questionPosition;

    public AnswersListViewAdapter(Context context, List<String> answers, int questionPosition) {
        this.context = context;
        this.answers = answers;
        this.questionPosition = questionPosition;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return answers.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(R.layout.row_answer_item, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.answerTextView.setText((i + 1) + "    " + answers.get(i));

        if (DataHolder.getDataHolder().answers.get(questionPosition) != 100) {
            if (DataHolder.getDataHolder().correctAnswers.get(questionPosition) == i)
                viewHolder.answerTextView.setBackgroundResource(R.drawable.design_textview_answer_correct);
            else if (DataHolder.getDataHolder().answers.get(questionPosition) == i)
                viewHolder.answerTextView.setBackgroundResource(R.drawable.design_textview_answer_incorrect);
            else
                viewHolder.answerTextView.setBackgroundResource(R.drawable.design_textview_answer);
        }

        return view;
    }

    private class ViewHolder {
        TextView answerTextView;

        public ViewHolder(View v) {
            answerTextView = (TextView) v.findViewById(R.id.answerTextView);
        }
    }
}