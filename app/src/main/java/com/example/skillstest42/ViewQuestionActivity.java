package com.example.skillstest42;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ViewQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText youranswer;
    private Button btnsend, btncancel;
    private TextView txtno, txtquestion, txtopt1, txtopt2, txtopt3,txtopt4;
    private String strquestionno, strquestion, stroption1, stroption2, stroption3, stroption4, stranswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_question);

        //
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //
        txtno = (TextView) findViewById(R.id.txtquestionno);
        txtquestion = (TextView) findViewById(R.id.txtquestion);
        txtopt1 = (TextView) findViewById(R.id.txtoption1);
        txtopt2 = (TextView) findViewById(R.id.txtoption2);
        txtopt3 = (TextView) findViewById(R.id.txtoption3);
        txtopt4 = (TextView) findViewById(R.id.txtoption4);
        youranswer = (EditText) findViewById(R.id.editanswer);
        btnsend = (Button) findViewById(R.id.btnsend);
        btncancel = (Button) findViewById(R.id.btncancel);

        //listeners

        btnsend.setOnClickListener(this);
        btncancel.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            strquestionno = bundle.getString("qno");
            strquestion = bundle.getString("question");
            stroption1 = bundle.getString("opt1");
            stroption2 = bundle.getString("opt2");
            stroption3 = bundle.getString("opt3");
            stroption4 = bundle.getString("opt4");

            txtno.setText("Question: "+strquestionno);
            txtquestion.setText(strquestion);
            txtopt1.setText("a) "+ stroption1);
            txtopt2.setText("b) " +stroption2);
            txtopt3.setText("c) "+stroption3);
            txtopt4.setText("d) "+stroption4);
        }
    }

    @Override
    public void onClick(View v) {
        int btnid = v.getId();

        switch (btnid){
            case R.id.btnsend:
                break;
            case R.id.btncancel:
                youranswer.setText(""); //clearing the edit text
                break;
        }
    }

    public void submitAnswer(){
        stranswer = youranswer.getText().toString();
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(stranswer);
        finish();
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{
        String addtourl;

        @Override
        protected void onPreExecute() {
            addtourl = "http://192.168.254.112/skillstest42/";
        }

        @Override
        protected String doInBackground(String... param) {
            String answer;
            answer = param[6];

            try{
                URL url = new URL("http://192.168.254.112/skillstest42/loginstudent.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                String myanswer = URLEncoder.encode("answer", "UTF-8");
                bufferedWriter.write(myanswer);
                bufferedWriter.flush();
                bufferedWriter.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                inputStream.close();
                httpURLConnection.disconnect();
                return "Answer is saved to database";

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String answer) {
            Toast.makeText(ViewQuestionActivity.this, answer+" is saved", Toast.LENGTH_SHORT).show();
        }


    }
}
