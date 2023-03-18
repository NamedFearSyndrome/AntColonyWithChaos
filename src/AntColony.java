import java.util.ArrayList;
import java.util.List;
import java.util.Random;
//import java.util.random.RandomGenerator;

public class AntColony {
    //int taskNum = 100;
    int taskNum = 100;
    ArrayList<Double> tasks = new ArrayList<>();

    //int nodeNum = 10;
    int nodeNum = 10;
    ArrayList<Double> nodes = new ArrayList<>();

    int taskLengthMin = 10;
    int taskLengthMax = 100;

    int nodeSpeedMin = 10;
    int nodeSpeedMax = 100;

    int iteratorNum = 100;

    int antNum = 100;

    ArrayList<List<Double>> timeMatrix = new ArrayList<>();

    ArrayList<List<Double>> pheromoneMatrix = new ArrayList<>();

    ArrayList<Integer> maxPheromoneMatrix = new ArrayList<>();

    ArrayList<Integer> criticalPointMatrix = new ArrayList<>();

    ArrayList<List<Double>> resultData = new ArrayList<>();

    double p = 0.5;
    double q = 1.5;

    int mode = 0;
    AntColony(){
        tasks = initRandomArray(taskNum, taskLengthMin, taskLengthMax);
        nodes = initRandomArray(nodeNum, nodeSpeedMin, nodeSpeedMax);
        aca();
//        int i = 0;
//        //输出迭代至最优解的最小迭代次数
//        for(i = 0; i < iteratorNum; ++i){
//            double temp = resultData.get(i).get(0);
//            boolean flag = true;
//            for(int j = 1; j < resultData.get(i).size(); ++j){
//                if(resultData.get(i).get(j) != temp){
//                    flag = false;
//                    break;
//                }
//            }
//            if(flag){
//                //test
//                //System.out.println(1);
//                break;
//            }
//            else{
//                continue;
//            }
//        }
        //test 输出最短时间
        //System.out.println(resultData.get(i).get(0));
//        for(List<Double> row : resultData){
//            for(Double n : row){
//                System.out.println(n);
//            }
//            System.out.println("\n");
//        }
//        ArrayList<Double> res = new ArrayList<Double>();
//        res.add((double)i);
        //res.add(1.0);
//        if(i >= iteratorNum){
//            System.out.println(i);
//            res.add(0.0);
//        }
//        else res.add(resultData.get(i).get(0));
//
//        return res;
        for(List<Double> row : resultData){
            for(Double n : row){
                System.out.println(n);
            }
            System.out.println("\n");
        }
    }
    void aca(){
        initTimeMatrix();
        initPheromoneMatrix();
        acaSearch();
    }
    private void acaSearch(){
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
            updatePheromoneMatrix(pathMatrix_allAnt, timeArray_oneIt);
//            switch (mode){
//                case 0:
//                    updatePheromoneMatrix(pathMatrix_allAnt, timeArray_oneIt);
//                case 1:
//                    updatePheromoneMatrixChaos(pathMatrix_allAnt, timeArray_oneIt);
//                default:
//                    updatePheromoneMatrix(pathMatrix_allAnt, timeArray_oneIt);
//            }
        }
    }
    void updatePheromoneMatrixChaos(ArrayList<List<List<Integer>>> pathMatrix_allAnt, ArrayList<Double> timeArray_oneIt){
        for(List<Double> prow : pheromoneMatrix){
            for(double num : prow) num *= p;
        }
        double minTime = Double.MAX_VALUE;
        int minIndex = -1;
        for(int antIndex = 0; antIndex < antNum; ++antIndex){
            if(timeArray_oneIt.get(antIndex) < minTime){
                minTime = timeArray_oneIt.get(antIndex);
                minIndex = antIndex;
            }
        }
        LogisticChaos l = new LogisticChaos(taskNum, nodeNum);
        for (int taskIndex=0; taskIndex<taskNum; taskIndex++) {
            for (int nodeIndex=0; nodeIndex<nodeNum; nodeIndex++) {
                if (pathMatrix_allAnt.get(minIndex).get(taskIndex).get(nodeIndex) == 1) {

                    //System.out.println();
                    pheromoneMatrix.get(taskIndex).set(nodeIndex, q * pheromoneMatrix.get(taskIndex).get(nodeIndex));
                }
            }
        }
        //将所有走过路径增加信息素
        for(int antIndex = 0; antIndex < antNum; antIndex++) {
            for (int taskIndex = 0; taskIndex < taskNum; taskIndex++) {
                for (int nodeIndex = 0; nodeIndex < nodeNum; nodeIndex++) {
                    if (pathMatrix_allAnt.get(antIndex).get(taskIndex).get(nodeIndex) == 1) {
                        //System.out.println(l.x.get(taskIndex * nodeNum + nodeIndex));
                        pheromoneMatrix.get(taskIndex).set(nodeIndex, 1 * pheromoneMatrix.get(taskIndex).get(nodeIndex) + 0.2 * l.x.get(taskIndex * nodeNum + nodeIndex) - 0.1);
                    }
                }
            }
        }
        maxPheromoneMatrix.clear();
        criticalPointMatrix.clear();
        for (int taskIndex=0; taskIndex<taskNum; taskIndex++) {
            double maxPheromone = pheromoneMatrix.get(taskIndex).get(0);
            int maxIndex = 0;
            double sumPheromone = pheromoneMatrix.get(taskIndex).get(0);
            boolean isAllSame = true;

            for (int nodeIndex=1; nodeIndex<nodeNum; nodeIndex++) {
                if (pheromoneMatrix.get(taskIndex).get(nodeIndex) > maxPheromone) {
                    maxPheromone = pheromoneMatrix.get(taskIndex).get(nodeIndex);
                    maxIndex = nodeIndex;
                }

                if (pheromoneMatrix.get(taskIndex).get(nodeIndex) != pheromoneMatrix.get(taskIndex).get(nodeIndex - 1)){
                    isAllSame = false;
                }

                sumPheromone += pheromoneMatrix.get(taskIndex).get(nodeIndex);
            }

            // 若本行信息素全都相等，则随机选择一个作为最大信息素
            if (isAllSame) {
                Random r = new Random();
                maxIndex = r.nextInt(nodeNum);
                //maxIndex = r.nextInt(0, nodeNum - 1);
                maxPheromone = pheromoneMatrix.get(taskIndex).get(maxIndex);
            }

            // 将本行最大信息素的下标加入maxPheromoneMatrix
            maxPheromoneMatrix.add(maxIndex);

            // 将本次迭代的蚂蚁临界编号加入criticalPointMatrix(该临界点之前的蚂蚁的任务分配根据最大信息素原则，而该临界点之后的蚂蚁采用随机分配策略)
            criticalPointMatrix.add((int) Math.round(antNum * (maxPheromone/sumPheromone)));

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
    void updatePheromoneMatrix(ArrayList<List<List<Integer>>> pathMatrix_allAnt, ArrayList<Double> timeArray_oneIt){
        for(List<Double> prow : pheromoneMatrix){
            for(double num : prow) num *= p;
        }
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
                    pheromoneMatrix.get(taskIndex).set(nodeIndex, q * pheromoneMatrix.get(taskIndex).get(nodeIndex)/*加入混沌系数乘混沌变量*/);
                }
            }
        }
        //将所有走过路径增加信息素
//        for(int antIndex = 0; antIndex < antNum; antIndex++) {
//            for (int taskIndex = 0; taskIndex < taskNum; taskIndex++) {
//                for (int nodeIndex = 0; nodeIndex < nodeNum; nodeIndex++) {
//                    if (pathMatrix_allAnt.get(antIndex).get(taskIndex).get(nodeIndex) == 1) {
//                        pheromoneMatrix.get(taskIndex).set(nodeIndex, q * pheromoneMatrix.get(taskIndex).get(nodeIndex)/*加入混沌系数乘混沌变量*/);
//                    }
//                }
//            }
//        }
        maxPheromoneMatrix.clear();
        criticalPointMatrix.clear();
        for (int taskIndex=0; taskIndex<taskNum; taskIndex++) {
            double maxPheromone = pheromoneMatrix.get(taskIndex).get(0);
            int maxIndex = 0;
            double sumPheromone = pheromoneMatrix.get(taskIndex).get(0);
            boolean isAllSame = true;

            for (int nodeIndex=1; nodeIndex<nodeNum; nodeIndex++) {
                if (pheromoneMatrix.get(taskIndex).get(nodeIndex) > maxPheromone) {
                    maxPheromone = pheromoneMatrix.get(taskIndex).get(nodeIndex);
                    maxIndex = nodeIndex;
                }

                if (pheromoneMatrix.get(taskIndex).get(nodeIndex) != pheromoneMatrix.get(taskIndex).get(nodeIndex - 1)){
                    isAllSame = false;
                }

                sumPheromone += pheromoneMatrix.get(taskIndex).get(nodeIndex);
            }

            // 若本行信息素全都相等，则随机选择一个作为最大信息素
            if (isAllSame) {
                Random r = new Random();
                maxIndex = r.nextInt(nodeNum);
                //maxIndex = r.nextInt(0, nodeNum - 1);
                maxPheromone = pheromoneMatrix.get(taskIndex).get(maxIndex);
            }

            // 将本行最大信息素的下标加入maxPheromoneMatrix
            maxPheromoneMatrix.add(maxIndex);

            // 将本次迭代的蚂蚁临界编号加入criticalPointMatrix(该临界点之前的蚂蚁的任务分配根据最大信息素原则，而该临界点之后的蚂蚁采用随机分配策略)
            criticalPointMatrix.add((int) Math.round(antNum * (maxPheromone/sumPheromone)));
            //System.out.println(maxPheromone);
        }

        //test 格式化输出信息素矩阵
//        for (int taskIndex=0; taskIndex<taskNum; taskIndex++) {
//            for(int nodeIndex = 0; nodeIndex < nodeNum; nodeIndex++){
//                System.out.printf("%f\t",pheromoneMatrix.get(taskIndex).get(nodeIndex));
//            }
//            System.out.printf("\n");
//        }
//        System.out.printf("\n");
    }
    ArrayList<Double> callTime_oneIt(ArrayList<List<List<Integer>>> pathMatrix_allAnt){
        ArrayList<Double> ret = new ArrayList<Double>();
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
        if(criticalPointMatrix.size() > taskCount && antCount <= criticalPointMatrix.get(taskCount)) return maxPheromoneMatrix.get(taskCount);
        Random r = new Random();
        return r.nextInt(nodeNum);
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
        for(int i = 0; i < taskNum; ++i){
            ArrayList<Double> row = new ArrayList<>();
            for(int j = 0; j < nodeNum; ++j)   row.add(tasks.get(i) / nodes.get(j));
            timeMatrix.add(row);
        }
    }
    void initPheromoneMatrix(){
        for(int i = 0; i < taskNum; ++i){
            ArrayList<Double> row = new ArrayList<>();
            for(int j = 0; j < nodeNum; ++j)   row.add(1.0);
            pheromoneMatrix.add(row);
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