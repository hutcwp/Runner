package com.hut.cwp.runner.homepage.clazz;

import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;

import org.xml.sax.XMLReader;

public class SizeLabel implements Html.TagHandler {
    private int size;

    public SizeLabel(int size) {
        this.size = size;
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        /**
            * 参数：
            * opening：为true时表示开始解析,为false时表示解析完
            * tag:当前解析的标签
            * output:文本中的内容
            * xmlReader:xml解析器
         **/
        // 判断当前解析的tag是否为size,并且已经解析完毕(在没有解析完毕之前output中没有数据)
        if(tag.toLowerCase().equals("size") && !opening) {
            // 通过output调用setSpan方法,改变文本的0下标到最后的下标的大小;
            // 最后的参数用来标识在span范围内的文本前后输入新的字符时是否也改变它们的效果;
            Log.d("TAG","size里面的内容在这里"+output);
            output.setSpan(new AbsoluteSizeSpan(size), 0, output.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
}