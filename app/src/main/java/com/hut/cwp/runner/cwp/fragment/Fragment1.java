package com.hut.cwp.runner.cwp.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hut.cwp.runner.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Adminis on 2017/3/7.
 */

public class Fragment1 extends Fragment {


    private LineChartView mChartView;

    private Axis axisY, axisX;
    private LineChartData chartData;
    private List<PointValue> pointValues;
    private ArrayList<AxisValue> axisValuesX;
    private ArrayList<AxisValue> axisValuesY;
    private List<Line> lines;
    private Line line;
    private Line line1;

    private Random random = new Random();


    private Button btn_back;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view=inflater.inflate(R.layout.activity_history, container, false);

        btn_back = (Button) view.findViewById(R.id.btn_back);

        initView(view);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().finish();
            }
        });
        return view;
    }
    /**
     * 初始化节点信息
     */

    public void initPointValues() {

        for (int num = 0; num < 15; num++) {

            pointValues.add(new PointValue(num, random.nextInt(10)));// 添加节点数据

        }


        for (int width = 0; width < 15; width++) {//循环为节点、X、Y轴添加数据

            // 添加Y轴显示的刻度值
            axisValuesX.add(new AxisValue(width).setValue(width));// 添加X轴显示的刻度值
        }

        for (int height = 0; height < 9; height++) {
            axisValuesY.add(new AxisValue(height).setValue(height));
        }


    }



    private void setChart() {

        mChartView.setZoomEnabled(false);//设置是否支持缩放
//        mChartView.setOnValueTouchListener(LineChartOnValueSelectListener touchListener);//为图表设置值得触摸事件
        mChartView.setInteractive(true);//设置图表是否可以与用户互动
        mChartView.setValueSelectionEnabled(true);//设置图表数据是否选中进行显示
        mChartView.setScrollEnabled(true);
    }

    private void setAxist() {
//        axisY.setName("Y");//设置Y轴显示名称
//        axisX.setName("X");//设置X轴显示名称
        axisX.setValues(axisValuesX);//为X轴显示的刻度值设置数据集合
        axisY.setValues(axisValuesY);
        axisX.setLineColor(Color.parseColor("#aab2bd"));// 设置X轴轴线颜色
        axisY.setLineColor(Color.parseColor("#aab2bd"));// 设置Y轴轴线颜色
        axisX.setTextColor(Color.BLACK);// 设置X轴文字颜色
        axisY.setTextColor(Color.BLACK);// 设置Y轴文字颜色
//        axisX.setTextSize(14);// 设置X轴文字大小
//        axisX.setTypeface(Typeface.DEFAULT);// 设置文字样式，此处为默认
//        axisX.setHasTiltedLabels(bolean isHasTit);// 设置X轴文字向左旋转45度
//        axisX.setHasLines(boolean isHasLines);// 是否显示X轴网格线
//        axisY.setHasLines(boolean isHasLines);// 是否显示Y轴网格线
//        axisX.setHasSeparationLine(true);// 设置是否有分割线
        axisX.setInside(false);// 设置X轴文字是否在X轴内部
    }

    private void setCharData() {


        chartData.setAxisYLeft(axisY);// 将Y轴属性设置到左边
        chartData.setAxisXBottom(axisX);// 将X轴属性设置到底部
//        chartData.setAxisYRight(axisYRight);//设置右边显示的轴
//        chartData.setAxisXTop(axisXTop);//设置顶部显示的轴
        chartData.setBaseValue(20);// 设置反向覆盖区域颜色
        chartData.setValueLabelBackgroundAuto(false);// 设置数据背景是否跟随节点颜色
        chartData.setValueLabelBackgroundColor(Color.BLUE);// 设置数据背景颜色
        chartData.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
        chartData.setValueLabelsTextColor(Color.BLACK);// 设置数据文字颜色
        chartData.setValueLabelTextSize(10);// 设置数据文字大小
        chartData.setValueLabelTypeface(Typeface.MONOSPACE);// 设置数据文字样式


    }

    private void setLine() {
        line1 = new Line(pointValues);
        //将值设置给折线
        line = line1;
        line.setColor(Color.RED);// 设置折线颜色
        line.setStrokeWidth(1);// 设置折线宽度
        line.setFilled(false);// 设置折线覆盖区域是否填充
        line.setCubic(true);// 是否设置为立体的
        line.setPointColor(Color.BLACK);// 设置节点颜色
        line.setPointRadius(2);// 设置节点半径

        line.setHasLabels(false);// 是否显示节点数据
        line.setHasLines(true);// 是否显示折线
        line.setHasPoints(true);// 是否显示节点

        line.setShape(ValueShape.CIRCLE);// 节点图形样式 DIAMOND菱形、SQUARE方形、CIRCLE圆形

        line.setHasLabelsOnlyForSelected(true);// 隐藏数据，触摸可以显示

        lines.add(line); // 将数据集合添加线
    }

    private void initView(View view) {

        mChartView = (LineChartView) view.findViewById(R.id.chart);
        // 节点数据结合
        pointValues = new ArrayList<>();
        axisY = new Axis().setHasLines(true);// Y轴属性
//        axisY.setMaxLabelChars(1);


        axisX = new Axis();// X轴属性

        //定义X轴刻度值的数据集合
        axisValuesX = new ArrayList<>();
        //定义Y轴刻度值的数据集合
        axisValuesY = new ArrayList<>();
        //定义节点信息
//        pointValues.add(new PointValue(0, 0));
        //定义线的集合
        lines = new ArrayList<>();
        //定义图表数据
        chartData = new LineChartData(lines);//将线的集合设置为折线图的数据


        initPointValues();

        setLine();


        setChart();
        setAxist();
        setCharData();


        Log.e("TAG", mChartView + "");
        mChartView.setLineChartData(chartData);//为图表设置数据，数据类型为LineChartData
        mChartView.setCurrentViewport(initViewPort(0, 10));
    }


    /**
     * 当前显示区域
     *
     * @param left
     * @param right
     * @return
     */
    private Viewport initViewPort(float left, float right) {
        Viewport port = new Viewport();
        port.top = 10;
        port.bottom = 0;
        port.left = left;
        port.right = right;
        return port;
    }


}
