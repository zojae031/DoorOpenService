package com.example.user.dooropenservice.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.user.dooropenservice.R;
import com.example.user.dooropenservice.app.DoorOpenService.DoorOpenService;
import com.example.user.dooropenservice.app.Model.CompanyVO;
import com.example.user.dooropenservice.app.Model.UserVO;
import com.example.user.dooropenservice.app.Server.ServerCallbackInterface.ICompanyCheckCallback;
import com.example.user.dooropenservice.app.Server.ServerCallbackInterface.ILogoutCallback;
import com.example.user.dooropenservice.app.Server.ServerConnection.ServerCompanyCheck;
import com.example.user.dooropenservice.app.Server.ServerConnection.ServerLogOut;

import java.util.ArrayList;

/*
 * MainActivity
 * 어플에대한 설명과 로그아웃기능이 있는 메인 엑티비티 클래스
 * function : LoginActivity에서 받은 정보를 가져와 로그아웃정보로 사용한다(SharedPreference)
 * 상호작용 : LoginActivity , ServerLogOut
 * @Author : 조재영
 */
public class MainActivity extends AppCompatActivity {
    BluetoothAdapter bluetoothAdapter;


    Button logOutBtn;//로그아웃 버튼
    Button managerBtn;

    ServerLogOut serverLogOut;//서버에서 flag 를 바꾸기 위한 로그아웃 스레드
    UserVO user;

    ILogoutCallback LogoutCallback;//로그아웃에 대한 콜백
    ICompanyCheckCallback companyCheckCallback;//company 정보에 대한 콜백

    SharedPreferences preferences; //로그인정보를 임시로 담을 내부 데이터베이스

    ServerCompanyCheck serverCompanyCheck;//서버에서 사용자의 company정보를 가져오는 클래스
    Context context;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        //로그인정보를 가져오는 작업
        intent = getIntent();
        user = (UserVO) intent.getSerializableExtra("userVO");

        setManageButton();

        //블루투스 이용 가능상태 확인
        CheckingBluetoothState();

        //로그아웃 버튼에 대한 셋팅
        LogoutSetting();

        //Server 에서 현재 로그인된 사용자의 company 정보를 가져오기
        getServerCompanyData();


    }

    @Override
    public void onBackPressed() {
        return;
    }

    private void CheckingBluetoothState() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish(); //서비스 종료
        }
        //블루투스 사용상태가 아닐경우 블루투스 사용상태 Dialog 출력
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivity(enableIntent);
        }
    }

    private void LogoutSetting() {
        //로그아웃 콜백 구현
        LogoutCallback = new ILogoutCallback() {
            @Override
            public void ServerConnectionError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "서버가 열려있지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void ServerError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "서버연결이 끊겼습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
    }

    private void deletePreferencesData() {
        preferences = getSharedPreferences("LoginInfo", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("id");
        editor.apply();
    }

    private void getServerCompanyData() {
        companyCheckCallback = new ICompanyCheckCallback() {
            @Override
            public void ServerConnectionError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "서버가 열려있지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void startService(ArrayList<CompanyVO> companyVOArrayList) {
                //DoorOpenService 실행 ->getServerCompanyData 에서 정보를 받아 실행시키는 형태로 제작
                Intent intent = new Intent(getApplicationContext(), DoorOpenService.class);
                intent.putExtra("ArrayList", companyVOArrayList);
                context.startService(intent);
            }
        };
        serverCompanyCheck = new ServerCompanyCheck(user, companyCheckCallback);
        serverCompanyCheck.start();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:   //ID 확인하는 과정
                logOutBtn = findViewById(R.id.logout);
                logOutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        serverLogOut = new ServerLogOut(user, LogoutCallback);
                        serverLogOut.setName("serverLogout");
                        serverLogOut.start();
                        //로그아웃을 하면 정보를 지운다
                        deletePreferencesData();
                        stopService(new Intent(getApplicationContext(), DoorOpenService.class));
                        finish();
                    }
                });
        }
    }

    public void setManageButton(){
        String userId = user.getId();

        managerBtn = (Button) findViewById(R.id.manager);

        if(userId.equals("admin")){
            managerBtn.setVisibility(View.VISIBLE);
        }
        else{
            managerBtn.setVisibility(View.INVISIBLE);
        }

        managerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
