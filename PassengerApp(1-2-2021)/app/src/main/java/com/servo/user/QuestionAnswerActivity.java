package com.servo.user;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.adapter.files.QuestionAnswerEAdapter;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.utils.Utils;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestionAnswerActivity extends AppCompatActivity {

    public GeneralFunctions generalFunc;
    TextView titleTxt;
    ImageView backImgView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    ExpandableListView expandableList;

    QuestionAnswerEAdapter adapter;

    private int lastExpandedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer);

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());


        titleTxt = (TextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);

        expandableList = (ExpandableListView) findViewById(R.id.list);

        expandableList.setDividerHeight(2);
        expandableList.setGroupIndicator(null);
        expandableList.setClickable(true);

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        adapter = new QuestionAnswerEAdapter(getActContext(), listDataHeader, listDataChild);

        expandableList.setAdapter(adapter);


        expandableList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expandableList.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        backImgView.setOnClickListener(new setOnClickList());

        setData();
    }

    public void setData() {

        titleTxt.setText(generalFunc.getJsonValue("vTitle", getIntent().getStringExtra("QUESTION_LIST")));
        JSONArray obj_ques = generalFunc.getJsonArray("Questions", getIntent().getStringExtra("QUESTION_LIST"));
        for (int i = 0; i < obj_ques.length(); i++) {
            JSONObject obj_temp = generalFunc.getJsonObject(obj_ques, i);


            listDataHeader.add(generalFunc.getJsonValueStr("vTitle", obj_temp));

            List<String> answer = new ArrayList<String>();
            answer.add(generalFunc.getJsonValueStr("tAnswer", obj_temp));

            listDataChild.put(listDataHeader.get(i), answer);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public Context getActContext() {
        return QuestionAnswerActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Utils.hideKeyboard(getActContext());
            switch (view.getId()) {
                case R.id.backImgView:
                    QuestionAnswerActivity.super.onBackPressed();
                    break;

            }
        }
    }
}
