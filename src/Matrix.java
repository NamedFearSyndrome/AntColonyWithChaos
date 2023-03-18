import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Matrix {
    private int taskNum = 100;
    private ArrayList<Double> tasks = new ArrayList<>();
    private int nodeNum = 10;
    private ArrayList<Double> nodes = new ArrayList<>();
    private int taskLengthMin = 10;
    private int taskLengthMax = 100;
    private int nodeSpeedMin = 10;
    private int nodeSpeedMax = 100;
    private ArrayList<List<Double>> MCT = new ArrayList<>();
    Matrix(){
        tasks = initRandomArray(taskNum, taskLengthMin, taskLengthMax);
        nodes = initRandomArray(nodeNum, nodeSpeedMin, nodeSpeedMax);
        initTimeMatrix();
    }
    ArrayList<List<Double>> getMCT(){
        return MCT;
    }
    int getTaskLengthMin(){
        return taskLengthMin;
    }
    int getTaskLengthMax(){
        return taskLengthMax;
    }
    int getNodeSpeedMin(){
        return nodeSpeedMin;
    }
    int getNodeSpeedMax(){
        return nodeSpeedMax;
    }
    ArrayList<Double> initRandomArray(int length, int min, int max){
        ArrayList<Double> ret = new ArrayList<>();
        Random r = new Random();
        for(int i = 0; i < length; ++i){
            int temp = r.nextInt(max - min + 1) + min;
            ret.add((double)temp);
        }
        return ret;
    }
    void initTimeMatrix(){
        MCT.clear();
        for(int i = 0; i < taskNum; ++i){
            ArrayList<Double> row = new ArrayList<>();
            for(int j = 0; j < nodeNum; ++j)   row.add(tasks.get(i) / nodes.get(j));
            MCT.add(row);
        }
    }
}
