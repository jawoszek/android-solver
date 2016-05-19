package januszek.solver.Activities;

import android.graphics.DashPathEffect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.util.Arrays;

import januszek.solver.Problems.Problem;
import januszek.solver.R;

public class PlotActivity extends AppCompatActivity {

    private XYPlot plot;
    private Problem problem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        problem = Problem.sideStep;

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.plot);

        // create a couple arrays of y-values to plot:
        double x1 = 0;
        double x2 = problem.equations.get(0).end/problem.equations.get(0).equation.get(1);
        double y1 = problem.equations.get(0).end/problem.equations.get(0).equation.get(0);
        double y2 = 0;


        Number[] series2Numbers = {y1,(y1+y2)/2,y2};
        Number[] series2Doms = {x1,(x1+x2)/2,x2};

        x1 = 0;
        x2 = problem.equations.get(1).end/problem.equations.get(1).equation.get(1);
        y1 = problem.equations.get(1).end/problem.equations.get(1).equation.get(0);
        y2 = 0;

        Number[] series1Numbers = {y1,(y1+y2)/2,y2};
        Number[] series1Doms = {x1,(x1+x2)/2,x2};

        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Doms),
                Arrays.asList(series1Numbers), problem.equations.get(0).getText());
        XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Doms),
                Arrays.asList(series2Numbers), problem.equations.get(1).getText());

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        series1Format.setPointLabelFormatter(new PointLabelFormatter());
        series1Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_labels);

        LineAndPointFormatter series2Format = new LineAndPointFormatter();
        series2Format.setPointLabelFormatter(new PointLabelFormatter());
        series2Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_labels_2);

        // add an "dash" effect to the series2 line:
        series2Format.getLinePaint().setPathEffect(
                new DashPathEffect(new float[] {

                        // always use DP when specifying pixel sizes, to keep things consistent across devices:
                        PixelUtils.dpToPix(20),
                        PixelUtils.dpToPix(15)}, 0));

        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        series2Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);
        plot.addSeries(series2, series2Format);

        // reduce the number of range labels
        plot.setTicksPerRangeLabel(2);

        // rotate domain labels 45 degrees to make them more compact horizontally:
        plot.getGraphWidget().setDomainLabelOrientation(-45);
    }
}
