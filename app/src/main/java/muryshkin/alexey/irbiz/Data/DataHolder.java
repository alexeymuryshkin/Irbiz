package muryshkin.alexey.irbiz.Data;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 123 on 8/6/2016.
 */
public class DataHolder {

    private static DataHolder dataHolder;

    public static DataHolder getDataHolder() {
        if (dataHolder == null)
            dataHolder = new DataHolder();
        return dataHolder;
    }

    public List<Integer> answers;
    public List<Integer> correctAnswers;
    public int result;
    public int answered;

    public List<JSONObject> participants;
    public List<JSONObject> males;
    public List<JSONObject> females;

    public List<Integer> generatedList;

    public DataHolder() {
        correctAnswers = new LinkedList<>();
        answers = new LinkedList<>();
        result = 0;
        answered = 0;

        participants = new ArrayList<>();
        males = new ArrayList<>();
        females = new ArrayList<>();

        generatedList = new ArrayList<>();
    }
}
