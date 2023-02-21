import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AntColonyTest {
    public static void main(String[] arg){
//        AntColony test = new AntColony();
//        System.out.println(test.AntColony(0));
//        System.out.println(test.AntColony(1));

        AntColony2 test = new AntColony2();

        System.out.println(test.AntColony(100));

//        ArrayList<Double> res = new ArrayList<Double>(){
//            {
//                for(int i = 0; i < 6; i++)
//                    add(0.0);
//            }
//        };
//        int times = 200, task = 100;
//        for(; task <= 500; task += 100){
//            for(int i = 0; i < times; i++){
//                AntColony2 test = new AntColony2();
//                for(int j = 0; j < 6; j++)
//                    res.set(j, res.get(j) + test.AntColony(task).get(j));
//            }
//            System.out.println(task);
//            for(int j = 0; j < 6; j++)
//                System.out.println(res.get(j) / times);
//        }

//        LogisticChaos l = new LogisticChaos(100, 10);
//        for(int i = 0; i < 1000; ++i)
//            System.out.println(l.x.get(i));
        draw(test);
    }
    static void draw(AntColony2 ac){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1, "row1", "col1");
        dataset.addValue(2, "row1", "col2");
        dataset.addValue(3, "row2", "col1");
        dataset.addValue(6, "row2", "col2");

        JFreeChart chart = ChartFactory.createLineChart(
                "Example",
                "Time",
                "Time",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );

        ChartFrame chartFrame = new ChartFrame("Test", chart);

        chartFrame.pack();
        chartFrame.setVisible(true);
    }
}
