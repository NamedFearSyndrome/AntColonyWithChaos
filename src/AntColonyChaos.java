import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AntColonyChaos {
    //int taskNum = 100;

    private int taskNum;
    private ArrayList<Double> tasks = new ArrayList<>();
    private int nodeNum;
    private int iteratorNum;
    private int antNum;
    private int taskLengthMin;
    private int taskLengthMax;
    private int nodeSpeedMin;
    private int nodeSpeedMax;

    ArrayList<List<Double>> MCT = new ArrayList<>();

    ArrayList<List<Double>> pheromoneMatrix = new ArrayList<>();

    ArrayList<List<Double>> P = new ArrayList<>();
    //统计每一只蚂蚁某次迭代中总路径长度
    ArrayList<Double> antPathLength = new ArrayList<>();
    //记录某次迭代中，每个节点已经安排的工作时间
    ArrayList<Double> nodeTimeMatrix = new ArrayList<>();
    //记录数据
    ArrayList<List<Double>> resultData = new ArrayList<>();

    double p;
    double alpha;
    double beta;
    double Q;
    double c;

    ArrayList<List<Double>> timeData = new ArrayList<>();

    int iter0;
    LogisticChaos l;

    ArrayList<Double> testResult = new ArrayList<>();

    AntColonyChaos(Matrix m, common c){
        iteratorNum = c.getIteratorNum();
        antNum = c.getAntNum();
        p = c.getP();
        alpha = c.getAlpha();
        beta = c.getBeta();
        Q = c.getQ();
        iter0 = c.getIter0();

        MCT = m.getMCT();
        taskNum = MCT.size();
        nodeNum = MCT.get(0).size();
        nodeSpeedMax = m.getNodeSpeedMax();
        nodeSpeedMin = m.getNodeSpeedMin();
        taskLengthMax = m.getTaskLengthMax();
        taskLengthMin = m.getTaskLengthMin();
        l = new LogisticChaos(taskNum, nodeNum);
//        for(int i = 0; i < MCT.size(); ++i){
//            System.out.print(i);
//            System.out.print(MCT.get(i) + "\n");
//        }
        aca();

        double minTime = Double.MAX_VALUE;
        ArrayList<Double> minResultData = new ArrayList<Double>();
        for(int i = 0; i < iteratorNum; i++){
            //System.out.print(i + " " + Collections.min(resultData.get(i)) + "\n");
            double temp = Collections.min(resultData.get(i));
            System.out.print(temp + "\n");
            minTime = Math.min(minTime, temp);
            minResultData.add(minTime);
        }
        System.out.println();
        for(int i = 0; i < iteratorNum; i++){
            //System.out.print(i + " " + Collections.min(resultData.get(i)) + "\n");
            System.out.print(minResultData.get(i) + "\n");
        }
        System.out.println();

    }
    void aca(){
        initPheromoneMatrixChaos();
        for(int i = 0; i < nodeNum; i++){
            nodeTimeMatrix.add(0.0);
        }
        P = initDoubleMatrix(taskNum, nodeNum, 0.0);
        timeData = initDoubleMatrix(iteratorNum, 2, 0.0);
        resultData.clear();
        acaSearch();
    }
    private void acaSearch(){
        double minTime = Double.MAX_VALUE;
        for(int itCount = 0; itCount < iteratorNum; ++itCount){
            ArrayList<List<List<Integer>>> pathMatrix_allAnt = new ArrayList<>();
            for(int i = 0; i < nodeNum; i++){
                nodeTimeMatrix.set(i, 0.0);
            }
            antPathLength.clear();
            for(int antCount = 0; antCount < antNum; ++antCount){
                ArrayList<List<Integer>> pathMatrix_oneAnt = initIntMatrix(taskNum, nodeNum, 0);
                double oneAntTime = 0;
                for(int taskCount = 0; taskCount < taskNum; ++taskCount){
                    int nodeCount = assignOneTask(antCount, taskCount);
                    nodeTimeMatrix.set(nodeCount, nodeTimeMatrix.get(nodeCount) + MCT.get(taskCount).get(nodeCount));
                    pathMatrix_oneAnt.get(taskCount).set(nodeCount, 1);
                    oneAntTime += MCT.get(taskCount).get(nodeCount);
                }
                pathMatrix_allAnt.add(pathMatrix_oneAnt);
                antPathLength.add(oneAntTime);
            }
            ArrayList<Double> timeArray_oneIt = callTime_oneIt(pathMatrix_allAnt);
            resultData.add(timeArray_oneIt);
            if(itCount < iter0) updatePheromoneMatrixChaos(pathMatrix_allAnt, timeArray_oneIt);
            else updatePheromoneMatrix(pathMatrix_allAnt, timeArray_oneIt);
        }
    }
    void updatePheromoneMatrixChaos(ArrayList<List<List<Integer>>> pathMatrix_allAnt, ArrayList<Double> timeArray_oneIt){
//        for(List<Double> prow : pheromoneMatrix){
//            for(int i = 0; i < nodeNum; i++){
//                prow.set(i, prow.get(i) * p);
//            }
//        }
        double minTime = Double.MAX_VALUE;
        int minIndex = -1;
        for(int antIndex = 0; antIndex < antNum; ++antIndex){
            if(timeArray_oneIt.get(antIndex) < minTime){
                minTime = timeArray_oneIt.get(antIndex);
                minIndex = antIndex;
            }
        }
        //将最快的路径增加信息素
        for (int taskIndex=0; taskIndex<taskNum; taskIndex++) {
            for (int nodeIndex=0; nodeIndex<nodeNum; nodeIndex++) {
                if (pathMatrix_allAnt.get(minIndex).get(taskIndex).get(nodeIndex) == 1) {
                    pheromoneMatrix.get(taskIndex).set(nodeIndex, pheromoneMatrix.get(taskIndex).get(nodeIndex) * p +
                                Q / antPathLength.get(minIndex) + c * (2 * l.x.get(taskIndex * nodeNum + nodeIndex) - 1));
                }
            }
        }
        //将所有走过路径增加信息素
//        for(int antIndex = 0; antIndex < antNum; antIndex++) {
//            for (int taskIndex = 0; taskIndex < taskNum; taskIndex++) {
//                for (int nodeIndex = 0; nodeIndex < nodeNum; nodeIndex++) {
//                    if (pathMatrix_allAnt.get(antIndex).get(taskIndex).get(nodeIndex) == 1) {
//                        pheromoneMatrix.get(taskIndex).set(nodeIndex, pheromoneMatrix.get(taskIndex).get(nodeIndex) * p + Q / antPathLength.get(antIndex));
//                    }
//                }
//            }
//        }
    }
    void updatePheromoneMatrix(ArrayList<List<List<Integer>>> pathMatrix_allAnt, ArrayList<Double> timeArray_oneIt){
//        for(List<Double> prow : pheromoneMatrix){
//            for(int i = 0; i < nodeNum; i++){
//                prow.set(i, prow.get(i) * p);
//            }
//        }
        //
        double minTime = Double.MAX_VALUE;
        int minIndex = -1;
        for(int antIndex = 0; antIndex < antNum; ++antIndex){
            if(timeArray_oneIt.get(antIndex) < minTime){
                minTime = timeArray_oneIt.get(antIndex);
                minIndex = antIndex;
            }
        }
        //将最快的路径增加信息素
        for (int taskIndex=0; taskIndex<taskNum; taskIndex++) {
            for (int nodeIndex=0; nodeIndex<nodeNum; nodeIndex++) {
                if (pathMatrix_allAnt.get(minIndex).get(taskIndex).get(nodeIndex) == 1) {
                    pheromoneMatrix.get(taskIndex).set(nodeIndex, pheromoneMatrix.get(taskIndex).get(nodeIndex) * p + Q / antPathLength.get(minIndex));
                }
            }
        }
        //将所有走过路径增加信息素
//        for(int antIndex = 0; antIndex < antNum; antIndex++) {
//            for (int taskIndex = 0; taskIndex < taskNum; taskIndex++) {
//                for (int nodeIndex = 0; nodeIndex < nodeNum; nodeIndex++) {
//                    if (pathMatrix_allAnt.get(antIndex).get(taskIndex).get(nodeIndex) == 1) {
//                        pheromoneMatrix.get(taskIndex).set(nodeIndex, pheromoneMatrix.get(taskIndex).get(nodeIndex) * p + Q / antPathLength.get(antIndex));
//                    }
//                }
//            }
//        }
    }
    ///统计每一只蚂蚁某次迭代中，用时最长的节点耗时
    ArrayList<Double> callTime_oneIt(ArrayList<List<List<Integer>>> pathMatrix_allAnt){
        ArrayList<Double> ret = new ArrayList<>();
        for (List<List<Integer>> pathMatrix : pathMatrix_allAnt) {
            double maxTime = -1;
            for (int nodeIndex = 0; nodeIndex < nodeNum; ++nodeIndex) {
                double time = 0;
                for (int taskIndex = 0; taskIndex < taskNum; ++taskIndex) {
                    if (pathMatrix.get(taskIndex).get(nodeIndex) == 1) time += MCT.get(taskIndex).get(nodeIndex);
                }
                maxTime = Math.max(time, maxTime);
            }
            ret.add(maxTime);
        }
        return ret;
    }
    int assignOneTask(int antCount, int taskCount){
        double sum = 0;
        for(int nodeIndex = 0; nodeIndex < nodeNum; nodeIndex++){
            double temp = Math.pow(pheromoneMatrix.get(taskCount).get(nodeIndex), alpha) * Math.pow(1 / (MCT.get(taskCount).get(nodeIndex) + nodeTimeMatrix.get(nodeIndex)), beta);
            sum += temp;
            P.get(taskCount).set(nodeIndex, temp);
        }
        P.get(taskCount).set(0, P.get(taskCount).get(0) / sum);
        for(int nodeIndex = 1; nodeIndex < nodeNum; nodeIndex++){
            P.get(taskCount).set(nodeIndex, P.get(taskCount).get(nodeIndex) / sum + P.get(taskCount).get(nodeIndex - 1));
        }

        Random r = new Random();
        double num = r.nextDouble();
        for(int nodeIndex = 0; nodeIndex < nodeNum; nodeIndex++){
            if(num <= P.get(taskCount).get(nodeIndex)) return nodeIndex;
        }
        return nodeNum - 1;
    }
    ArrayList<List<Integer>> initIntMatrix(int n, int m, int defaultIntNum){
        ArrayList<List<Integer>> ret = new ArrayList<>();
        for(int i = 0; i < n; ++i){
            ArrayList<Integer> row = new ArrayList<>();
            for(int j = 0; j < m; ++j) row.add(defaultIntNum);
            ret.add(row);
        }
        return ret;
    }
    ArrayList<List<Double>> initDoubleMatrix(int n, int m, double defaultDoubleNum){
        ArrayList<List<Double>> ret = new ArrayList<>();
        for(int i = 0; i < n; ++i){
            ArrayList<Double> row = new ArrayList<>();
            for(int j = 0; j < m; ++j) row.add(defaultDoubleNum);
            ret.add(row);
        }
        return ret;
    }
    void initPheromoneMatrixChaos(){
        pheromoneMatrix.clear();
        LogisticChaos l = new LogisticChaos(taskNum, nodeNum);
        for(int i = 0; i < taskNum; ++i){
            ArrayList<Double> row = new ArrayList<>();
            for(int j = 0; j < nodeNum; ++j)
                //row.add(l.x.get(i * nodeNum + j) * (taskLengthMax / nodeSpeedMin - taskLengthMin / nodeSpeedMax) + taskLengthMin / nodeSpeedMax + 1);
                row.add(l.x.get(i * nodeNum + j) + 1);
            pheromoneMatrix.add(row);
        }
    }
}
