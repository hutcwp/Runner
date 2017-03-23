package com.hut.cwp.runner.history.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hut.cwp.runner.R;
import com.hut.cwp.runner.dao.DBUtils;
import com.hut.cwp.runner.dao.RunDailyData;
import com.hut.cwp.runner.dao.RunMouthData;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by Adminis on 2017/3/7.
 */

public class ChartHistoryFragment extends Fragment {


    private TextView text_chart_distance,text_chart_alltime,text_chart_calorie,text_chart_vector;

    //声明所需变量
    public final static String[] months = new String[]{"Jan", "Feb", "Mar",
            "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",};

    public final static String[] daily = new String[]{"1", "2", "3", "4", "5", "6", "7", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};

    ColumnChartView columnChart;
    ColumnChartData columnData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View view = inflater.inflate(R.layout.fragment_chart_history, container, false);

        initView(view);

        dataInit();

        readDataFromDB();

        return view;
    }


    public void readDataFromDB(){

        List<RunMouthData> list = DBUtils.getInstance(getActivity()).selectFromMouth();

        if (list.size()>0){

            upDateTextData(list.get(0));
        }

    }


    public void upDateTextData(RunMouthData data){

        text_chart_distance.setText(data.getAlldistance()+" m");
        text_chart_alltime.setText(data.getAlltime()+" h");
        text_chart_calorie.setText(data.getAllcalorie()+" ka");
        text_chart_vector.setText(data.getAllvector()+" m/s");

    }


    private void initView(View view) {

        columnChart = (ColumnChartView) view.findViewById(R.id.columnchart);

        text_chart_distance = (TextView) view.findViewById(R.id.text_chart_distance);
        text_chart_alltime = (TextView) view.findViewById(R.id.text_char_alltime);
        text_chart_calorie = (TextView) view.findViewById(R.id.text_chart_calorie);
        text_chart_vector= (TextView) view.findViewById(R.id.text_chart_vector);

    }




    //初始化数据并显示在图表上
    private void dataInit() {

        int numSubcolumns = 1;
//        int numColumns = months.length;

        List<AxisValue> axisValues = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;

        List<RunDailyData> datalist = DBUtils.getInstance(getActivity()).selectFromDaily();

        int numColumns = datalist.size();


        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<>();
            for (int j = 0; j < numSubcolumns; ++j) {

                RunDailyData data = datalist.get(i);
                if (data != null) {

                    float value = data.getDistance();

                    values.add(new SubcolumnValue(value,
                            ChartUtils.pickColor()));
                    Toasty.info(getActivity(), "数据已加载").show();
                } else {

                    Toasty.info(getActivity(), "历史数据为空，查询数据库得知").show();
                }

                // 点击柱状图就展示数据量
                if (i < daily.length - 1) {
                    axisValues.add(new AxisValue(i).setLabel(daily[i]));
                }
                columns.add(new Column(values).setHasLabelsOnlyForSelected(true));

            }


            columnData = new ColumnChartData(columns);


            columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true)
                    .setTextColor(Color.BLACK));
            columnData.setAxisYLeft(new Axis().setHasLines(true)
                    .setTextColor(Color.BLACK).setMaxLabelChars(2));
            columnChart.setColumnChartData(columnData);

            // Set value touch listener that will trigger changes for chartTop.
            columnChart.setOnValueTouchListener(new ValueTouchListener());
            // Set selection mode to keep selected month column highlighted.
            columnChart.setValueSelectionEnabled(true);
            columnChart.setZoomType(ZoomType.HORIZONTAL);

            columnChart.setZoomEnabled(false);
        }

    }
////    public List<Column> updataFromDB() {
////
////
//
//
//        int numSubcolumns = 1;
//        int numColumns = datalist.size();
//
//        axisValues = new ArrayList<>();
//        List<Column> columns = new ArrayList<>();
//        List<SubcolumnValue> values;
//
//
//        for (int i = 0; i < numColumns; ++i) {
//            values = new ArrayList<>();
//            for (int j = 0; j < numSubcolumns; ++j) {
//
//                RunDailyData data = datalist.get(i);
//                if (data != null) {
//
//                    float value = (float) Math.random() * 50f;
////                    float value = data.getDistance();
//
//                    values.add(new SubcolumnValue(value,
//                            ChartUtils.pickColor()));
//
//                }
//            }
//            // 点击柱状图就展示数据量
//            axisValues.add(new AxisValue(i).setLabel(months[i]));
//            columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
//        }
//
//        return columns;
//    }

    /**
     * 柱状图监听器
     *
     * @author 1017
     */
    private class ValueTouchListener implements
            ColumnChartOnValueSelectListener {
        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex,
                                    SubcolumnValue value) {
            // generateLineData(value.getColor(), 100);
        }

        @Override
        public void onValueDeselected() {
            // generateLineData(ChartUtils.COLOR_GREEN, 0);
        }
    }


}