package com.gmf.wedding;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AccountActivity extends AppCompatActivity {
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private EditText et_user_login, et_pw_login;
    private String username_login, pw_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        et_user_login = (EditText)findViewById(R.id.ET_user_login);
        et_pw_login = (EditText)findViewById(R.id.ET_pw_login);

        InitItems();
    }

    //初始化按鈕
    private void InitItems(){
        Button bt_login_login = (Button)findViewById(R.id.BT_login_login);
        bt_login_login.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                CheckUsers(); //按下登入鍵，進入確認帳號密碼程序
            }
        });

        Button bt_cancel_login = (Button)findViewById(R.id.BT_cancel_login);
        bt_cancel_login.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                GoGuest();
            }
        });
    }

    /* 按下登入時，檢查帳號跟密碼是否符合設定，
	 * 兩組帳號/密碼分別是 boss/boss 以及 emilia/emilia，
	 * 兩組帳號密碼之外按下登入鍵則出現提示訊息*/
    private void CheckUsers(){
        username_login = et_user_login.getText().toString();
        pw_login = et_pw_login.getText().toString();

        new AsyncLogin().execute(username_login,pw_login);
    }

    private class AsyncLogin extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(AccountActivity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                // Enter URL address where your php file resides
                url = new URL("http://192.168.1.103:80/wedding_management/login.inc.php");

            }catch (MalformedURLException e){
                e.printStackTrace();
                return "exception";
            }

            try{
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }

            try{
                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK){
                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null){
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());
                }else {
                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }finally{
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result){
            //this method will be running on UI thread

            pdLoading.dismiss();

            if(result.equalsIgnoreCase("boss")){
                FragmentUtils.sLoginNameIs = username_login;
                GoBoss();
            }else if(result.equalsIgnoreCase("true")){
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */
                FragmentUtils.sLoginNameIs = username_login;
                GoGuest();
            }else if (result.equalsIgnoreCase("false")){
                // If username and password does not match display a error message
                Toast.makeText(AccountActivity.this, "帳號或密碼錯誤", Toast.LENGTH_LONG).show();
            }else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")){
                Toast.makeText(AccountActivity.this, "連線問題", Toast.LENGTH_LONG).show();
            }
        }
    }

    //進入管理者選項頁面top2
    private void GoBoss(){
        Intent intent = new Intent(AccountActivity.this,Top2Activity.class);
        startActivity(intent);
        AccountActivity.this.finish(); //關閉activity
    }

    //進入一般登入或無帳號按取消，進入頁面top1
    private void GoGuest(){
        Intent intent = new Intent(AccountActivity.this,Top1Activity.class);
        startActivity(intent);
        AccountActivity.this.finish();
    }

    //使用系統的倒退鍵，會顯示退出程式的提示視窗
    @Override
    public void onBackPressed(){
        AlertDialog.Builder ad=new AlertDialog.Builder(AccountActivity.this); //建立訊息方塊
        ad.setTitle("離開Emilia Hsiao");
        ad.setMessage("確定要離開?");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() { //按"是"退出程式
            public void onClick(DialogInterface dialog, int i) {
                AccountActivity.this.finish();
            }
        });
        ad.setNegativeButton("否",new DialogInterface.OnClickListener() { //按"否",則不執行任何操作
            public void onClick(DialogInterface dialog, int i) {
            }
        });
        ad.show();//顯示訊息視窗
    }

}
