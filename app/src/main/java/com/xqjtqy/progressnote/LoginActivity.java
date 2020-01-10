package com.xqjtqy.progressnote;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity {

    static final int LOGIN = 1;//登录
    static final int REGISTER = 2;//注册
    String responseData;
    String username;
    String password;
    private Handler handler;//用于进程间异步消息传递

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final Button registerButton = findViewById(R.id.register);
        final ProgressBar progressBar = findViewById(R.id.loading);

        handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(@NonNull Message msg) {//用于异步消息处理
                switch (msg.what) {
                    case LOGIN:
                    case REGISTER:
                        Toast.makeText(LoginActivity.this, responseData, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);//显示解析到的内容
                        break;
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//登录按钮事件处理
                progressBar.setVisibility(View.VISIBLE);//显示进度条
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                sendRequestWithOkHttpLogin(username, password);//通过OkHttp发送登录请求

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//注册按钮事件处理
                progressBar.setVisibility(View.VISIBLE);//显示进度条
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                sendRequestWithOkHttpRegister(username, password);//通过OkHttp发送注册请求
            }
        });
    }

    private void sendRequestWithOkHttpLogin(final String username, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {//在子线程中进行网络操作
                try {
                    OkHttpClient client = new OkHttpClient();//利用OkHttp发送HTTP请求调用服务端登录servlet
                    Request request = new Request.Builder().url("https://0kirby.cf:8443/progress_note_server/LoginServlet?username=" + username + "&password=" + password).build();
                    Response response = client.newCall(request).execute();
                    responseData = Objects.requireNonNull(response.body()).string();
                    Message message = new Message();
                    message.what = LOGIN;
                    handler.sendMessage(message);//通过handler发送消息请求toast

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void sendRequestWithOkHttpRegister(final String username, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {//在子线程中进行网络操作
                try {
                    OkHttpClient client = new OkHttpClient();//利用OkHttp发送HTTP请求调用服务端注册servlet
                    Request request = new Request.Builder().url("https://0kirby.cf:8443/progress_note_server/RegisterServlet?username=" + username + "&password=" + password).build();
                    Response response = client.newCall(request).execute();
                    responseData = Objects.requireNonNull(response.body()).string();
                    Message message = new Message();
                    message.what = LOGIN;
                    handler.sendMessage(message);//通过handler发送消息请求toast

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
