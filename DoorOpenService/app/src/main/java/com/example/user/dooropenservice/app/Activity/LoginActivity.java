package com.example.user.dooropenservice.app.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.dooropenservice.R;
import com.example.user.dooropenservice.app.Model.UserVO;
import com.example.user.dooropenservice.app.Server.ServerCallbackInterface.ILoginCallback;
import com.example.user.dooropenservice.app.Server.ServerConnection.ServerLogin;

/*
 * LoginActivity
 * 로그인동작과 서버통신이 이루어지는 Activity Class
 * 여기서는 Service 가 동작하지 않는다.
 * function : 로그인정보를 생성하고 서버와 통신한다.
 * 로그인에대한 콜백함수가 구현되어있다. <-> ServerLogin
 * @Author : 조재영
 */
public class LoginActivity extends Activity {


    private EditText ID, PassWord;
    private Button Login, Regist;
    private ServerLogin serverLogin;//서버와 연결하기위한 객체
    private ILoginCallback callback;//로그인 상황에 따른 콜백을 정의해주는 인터페이스 객체
    private UserVO user;

    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //기본셋팅
        checkingPermission();//권한여부 확인
        getViewInfo();//뷰에대한정보들을 가져온다

        //로그인에 관련된 메소드들
        checkLoginState();//현재 로그인상태를 확인하기 위함
        settingCallback(); //로그인 콜백메소드 셋팅
        setLoginButton(); //로그인 버튼 셋팅

        //회원가입에 관련된 메소드들
        setSignUpButton();//회원가입 버튼 셋팅

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private void getViewInfo(){
        Regist = findViewById(R.id.registration);
        ID = findViewById(R.id.id);
        PassWord = findViewById(R.id.password);
        Login = findViewById(R.id.button);

    }

    private void checkLoginState() {
        //앱을 메모리에서 제거 하였다 다시 실행할시 SharedPreference 를 확인하여 skip 한다.
        SharedPreferences preferences = getSharedPreferences("LoginInfo", 0);
        id = preferences.getString("id", "");
        if (!id.equals("")) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            user = new UserVO(id,null,null,null);
            intent.putExtra("userVO",user);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);

        }
    }



    private void settingCallback() {
        //로그인을 위한 콜백함수 구현
        callback = new ILoginCallback() {
            @Override
            public void StartService() {
                Intent MainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                user = new UserVO(ID.getText().toString(),PassWord.getText().toString(),null,null);
                MainActivityIntent.putExtra("userVO",user);//인텐트에 로그인에 대한 정보를 넣어줌


                //로그인이 되어 실행이 되면 현재 로그인정보를 저장한다.
                SharedPreferences preferences = getSharedPreferences("LoginInfo", 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("id", ID.getText().toString());
                editor.apply();

                startActivity(MainActivityIntent);
            }

            @Override
            public void FailToLogin() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.(사용자 로그인 중...)", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void NoData() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "사용자 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void ServerConnectionError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "서버가 열려있지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
    }

    public int getLocationMode() {
        try {
            return Settings.Secure.getInt(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void checkingPermission() {
        //위치 권한 동의 설정
        if (getLocationMode() == 0) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            Toast.makeText(getApplicationContext(), "높은 정확도 사용을 권장합니다.", Toast.LENGTH_LONG).show();
        }
        //위치정보 허가받기 (RunTime Permission Check)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    private void setSignUpButton(){
        //회원가입 버튼
        Regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
    private void setLoginButton(){
        //로그인 버튼
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //로그인 동작에 대한 로직
                user = new UserVO(ID.getText().toString(), PassWord.getText().toString(), null, null); //사용자 정보 저장
                if (user.getId().equals("")) {
                    Toast.makeText(getApplicationContext(), "아이디를 입력하시오", Toast.LENGTH_SHORT).show();
                }
                if (user.getPassword().equals("")) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력하시오", Toast.LENGTH_SHORT).show();
                }
                if (!user.getId().equals("") && !user.getPassword().equals("")) {//둘다 데이터가 있는 경우 서버 로그인 연결 시작
                    serverLogin = new ServerLogin(user, callback);
                    serverLogin.setName("ServerConnectionThread");
                    serverLogin.start();
                }

            }
        });
    }


}
