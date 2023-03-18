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
        Matrix m = new Matrix();
        common c = new common();
        AntColony3 test = new AntColony3(m, c);
//        AntColonyChaos acc = new AntColonyChaos(m, c);
//        AntColonyChaosSta accs = new AntColonyChaosSta(m, c);
    }
}
