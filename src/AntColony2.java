import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AntColony2 {
    //int taskNum = 100;
    int taskNum = 100;
    ArrayList<Double> tasks = new ArrayList<>();

    //int nodeNum = 10;
    int nodeNum = 12;
    ArrayList<Double> nodes = new ArrayList<>();

    int taskLengthMin = 10;
    int taskLengthMax = 100;

    int nodeSpeedMin = 10;
    int nodeSpeedMax = 100;

    int iteratorNum = 100;

    int antNum = 40;

    ArrayList<List<Double>> timeMatrix = new ArrayList<>();

    ArrayList<List<Double>> pheromoneMatrix = new ArrayList<>();

    ArrayList<List<Double>> P = new ArrayList<>();

    ArrayList<List<Double>> resultData = new ArrayList<>();

    double p = 0.1;
    double alpha = 1.5;
    double beta = 2;

    ArrayList<List<Double>> timeData = new ArrayList<>();

    ArrayList<Double> AntColony(int task){
        taskNum = task;
        tasks = initRandomArray(taskNum, taskLengthMin, taskLengthMax);
        nodes = initRandomArray(nodeNum, nodeSpeedMin, nodeSpeedMax);
        ArrayList<Double> res = new ArrayList<>();

        initTimeMatrix();
        for(int m = 0; m < 3; m++){
            aca(m);
//            int i = 0;
            //test 输出迭代至最优解的最小迭代次数
//            for(i = 0; i < iteratorNum; i++){
//                double temp = resultData.get(i).get(0);
//                boolean flag = true;
//                for(int j = 1; j < resultData.get(i).size(); ++j){
//                    if(Math.abs(resultData.get(i).get(j) - temp) >= 0.01){
//                        flag = false;
//                        break;
//                    }
//                }
//                if(flag){
//                    //test
//                    //System.out.println(1);
//                    break;
//                }
//                else{
//                    continue;
//                }
//            }

            //test 输出最短时间
            //System.out.println(resultData.get(i).get(0));

            //test 输出当前时间矩阵
//        for(List<Double> row : resultData){
//            for(Double n : row){
//                System.out.println(n);
//            }
//            System.out.println("\n");
//        }

            //System.out.println(i + "\n");

//            res.add((double)i);
//            if(i >= iteratorNum){
//                res.add(resultData.get(i - 1).get(0));
//            }
//            else res.add(resultData.get(i).get(0));

            //test 输出最终时间范围
            double minTime = Double.MAX_VALUE, maxTime =0.0 ;
            for(int i = 0; i < resultData.get(iteratorNum - 1).size(); ++i) {
                minTime = minTime > resultData.get(iteratorNum - 1).get(i) ? resultData.get(iteratorNum - 1).get(i) : minTime;
                maxTime = maxTime < resultData.get(iteratorNum - 1).get(i) ? resultData.get(iteratorNum - 1).get(i) : maxTime;
            }
            res.add(minTime);
            res.add(maxTime - minTime);

        }
        return res;
    }
    void aca(int mode){
        if(mode == 2)
            initPheromoneMatrixChaos();
        else
            initPheromoneMatrix();
        initP();
        resultData.clear();
        acaSearch(mode);
    }
    private void acaSearch(int mode){
        for(int itCount = 0; itCount < iteratorNum; ++itCount){
            ArrayList<List<List<Integer>>> pathMatrix_allAnt = new ArrayList<>();
            for(int antCount = 0; antCount < antNum; ++antCount){
                ArrayList<List<Integer>> pathMatrix_oneAnt = initMatrix(taskNum, nodeNum, 0);
                for(int taskCount = 0; taskCount < taskNum; ++taskCount){
                    int nodeCount = assignOneTask(antCount, taskCount);
                    pathMatrix_oneAnt.get(taskCount).set(nodeCount, 1);
                }
                pathMatrix_allAnt.add(pathMatrix_oneAnt);
            }
            ArrayList<Double> timeArray_oneIt = callTime_oneIt(pathMatrix_allAnt);
            //timeArray_oneIt.add((double)itCount);
            resultData.add(timeArray_oneIt);

            switch (mode){
                case 0:
                    updatePheromoneMatrix(pathMatrix_allAnt);
                case 1:
                    updatePheromoneMatrixChaos(pathMatrix_allAnt);
                case 2:
                    updatePheromoneMatrixChaos(pathMatrix_allAnt);
                default:
                    updatePheromoneMatrix(pathMatrix_allAnt);
            }
        }
    }
    void getData(){};
    void updatePheromoneMatrixChaos(ArrayList<List<List<Integer>>> pathMatrix_allAnt){
        for(List<Double> prow : pheromoneMatrix){
            for(double num : prow) num *= p;
        }
        LogisticChaos l = new LogisticChaos(taskNum, nodeNum);
        //将所有走过路径增加信息素
        for(int antIndex = 0; antIndex < antNum; antIndex++) {
            for (int taskIndex = 0; taskIndex < taskNum; taskIndex++) {
                for (int nodeIndex = 0; nodeIndex < nodeNum; nodeIndex++) {
                    if (pathMatrix_allAnt.get(antIndex).get(taskIndex).get(nodeIndex) == 1) {
                        //System.out.println(l.x.get(taskIndex * nodeNum + nodeIndex));
                        pheromoneMatrix.get(taskIndex).set(nodeIndex, 1 * pheromoneMatrix.get(taskIndex).get(nodeIndex) + 2 * l.x.get(taskIndex * nodeNum + nodeIndex));
                    }
                }
            }
        }
        //test 格式化输出信息素矩阵
//        for (int taskIndex =0; taskIndex<taskNum; taskIndex++) {
//            for(int nodeIndex = 0; nodeIndex < nodeNum; nodeIndex++){
//                System.out.printf("%f\t",pheromoneMatrix.get(taskIndex).get(nodeIndex));
//            }
//            System.out.printf("\n");
//        }
//        System.out.printf("\n");
    }
    void updatePheromoneMatrix(ArrayList<List<List<Integer>>> pathMatrix_allAnt){
        //test 格式化输出信息素矩阵
//        for (int taskIndex=0; taskIndex<taskNum; taskIndex++) {
//            for(int nodeIndex = 0; nodeIndex < nodeNum; nodeIndex++){
//                System.out.printf("%f\t",pheromoneMatrix.get(taskIndex).get(nodeIndex));
//            }
//            System.out.printf("\n");
//        }
//        System.out.printf("\n");

        for(List<Double> prow : pheromoneMatrix){
            for(double num : prow) num *= p;
        }
        //将所有走过路径增加信息素
        for(int antIndex = 0; antIndex < antNum; antIndex++) {
            for (int taskIndex = 0; taskIndex < taskNum; taskIndex++) {
                for (int nodeIndex = 0; nodeIndex < nodeNum; nodeIndex++) {
                    if (pathMatrix_allAnt.get(antIndex).get(taskIndex).get(nodeIndex) == 1) {
                        pheromoneMatrix.get(taskIndex).set(nodeIndex, pheromoneMatrix.get(taskIndex).get(nodeIndex) + 1/*加入混沌系数乘混沌变量*/);
                    }
                }
            }
        }
    }
    ArrayList<Double> callTime_oneIt(ArrayList<List<List<Integer>>> pathMatrix_allAnt){
        ArrayList<Double> ret = new ArrayList<>();
        for (List<List<Integer>> pathMatrix : pathMatrix_allAnt) {
            double maxTime = -1;
            for (int nodeIndex = 0; nodeIndex < nodeNum; ++nodeIndex) {
                double time = 0;
                for (int taskIndex = 0; taskIndex < taskNum; ++taskIndex) {
                    if (pathMatrix.get(taskIndex).get(nodeIndex) == 1) time += timeMatrix.get(taskIndex).get(nodeIndex);
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
            double temp = Math.pow(pheromoneMatrix.get(taskCount).get(nodeIndex), alpha) * Math.pow(1 / timeMatrix.get(taskCount).get(nodeIndex), beta);
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
    ArrayList<List<Integer>> initMatrix(int n, int m, int defaultNum){
        ArrayList<List<Integer>> ret = new ArrayList<>();
        for(int i = 0; i < n; ++i){
            ArrayList<Integer> row = new ArrayList<>();
            for(int j = 0; j < m; ++j) row.add(defaultNum);
            ret.add(row);
        }
        return ret;
    }
    void initTimeMatrix(){
        timeMatrix.clear();
        for(int i = 0; i < taskNum; ++i){
            ArrayList<Double> row = new ArrayList<>();
            for(int j = 0; j < nodeNum; ++j)   row.add(tasks.get(i) / nodes.get(j));
            timeMatrix.add(row);
        }
    }
    void initPheromoneMatrix(){
        pheromoneMatrix.clear();
        for(int i = 0; i < taskNum; ++i){
            ArrayList<Double> row = new ArrayList<>();
            for(int j = 0; j < nodeNum; ++j)   row.add(1.0);
            pheromoneMatrix.add(row);
        }
    }
    void initPheromoneMatrixChaos(){
        pheromoneMatrix.clear();
        LogisticChaos l = new LogisticChaos(taskNum, nodeNum);
        for(int i = 0; i < taskNum; ++i){
            ArrayList<Double> row = new ArrayList<>();
            for(int j = 0; j < nodeNum; ++j)   row.add(l.x.get(i * nodeNum + j));
            pheromoneMatrix.add(row);
        }
    }
    void initP(){
        P.clear();
        for(int i = 0; i < taskNum; ++i){
            ArrayList<Double> row = new ArrayList<>();
            for(int j = 0; j < nodeNum; ++j)   row.add(1.0);
            P.add(row);
        }
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
}
