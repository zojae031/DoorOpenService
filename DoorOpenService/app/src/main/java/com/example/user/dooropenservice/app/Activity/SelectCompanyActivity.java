package com.example.user.dooropenservice.app.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.user.dooropenservice.R;
import com.example.user.dooropenservice.app.Model.CompanyVO;
import com.example.user.dooropenservice.app.Model.UserVO;
import com.example.user.dooropenservice.app.Server.ServerCallbackInterface.IGetCompanyListCallback;
import com.example.user.dooropenservice.app.Server.ServerConnection.ServerGetCompanyList;

import java.util.ArrayList;

public class SelectCompanyActivity extends Activity {
    private Button finishBtn;
    private RadioGroup radioGroup;
    private String companyName = "sejong";
    private String textCompany = "세종대학교";
    Intent intent;
    private ArrayList<CompanyVO> companyVOArrayList;
    private IGetCompanyListCallback callback;

    ServerGetCompanyList serverGetCompanyList;
    private final int RESULT_OK = 100;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select_company);

        setContents();


        callback = new IGetCompanyListCallback() {

            //장소 선택 리스트뿌리기
            @Override
            public void sendData(final ArrayList<CompanyVO> companyVOArrayList) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < companyVOArrayList.size(); i++) {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            RadioButton radioButton = new RadioButton(getApplicationContext());
                            radioButton.setId(i);
                            radioButton.setLayoutParams(params);

                            radioButton.setText(companyVOArrayList.get(i).getCompany().replace("\"",""));
                            radioButton.setTextColor(Color.parseColor("#FFFFFF"));
                            radioGroup.addView(radioButton);
                        }
                    }
                });


                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        textCompany = companyVOArrayList.get(checkedId).getCompany().replace("\"","");
                        companyName = companyVOArrayList.get(checkedId).getCompany().replace("\"","");

                        Toast.makeText(getApplicationContext(), textCompany, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void ServerConnectionError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "서버 연결이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        serverGetCompanyList = new ServerGetCompanyList(new UserVO(null, null, null, null), callback);
        serverGetCompanyList.start();


        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("compName", companyName);
                intent.putExtra("textCompany", textCompany);
                Toast.makeText(getApplicationContext(), companyName, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {     //바깥영역 터치 안되게
        if (e.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    private void setContents() {
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        finishBtn = (Button) findViewById(R.id.finishBtn);

        intent = new Intent();

    }

    public void setList() {

    }

}

