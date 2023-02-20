import java.util.ArrayList;

public class LogisticChaos {
    double u = 4;
    double x0 = 0.1;
    ArrayList<Double> x = new ArrayList<>();
    LogisticChaos(int taskNum, int nodeNum){
        for(int i = 0; i < 500; ++i){
            x0 = u * x0 * (1 - x0);
            x.add(x0);
        }
        for(int i = 0; i < taskNum * nodeNum; ++i){
            x.add(u * x.get(i) * (1 - x.get(i)));
        }
    }
}
