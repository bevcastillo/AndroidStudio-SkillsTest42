package com.example.skillstest42;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    ArrayList<QuestionItem> arrayList = new ArrayList<QuestionItem>();
    ArrayAdapter<QuestionItem> adapter;
    String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //allow a parallel thread to run alongside
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        //
        listView = findViewById(R.id.listview_questions);
        adapter = new ArrayAdapter<QuestionItem>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        localhostDialog();

    }

    public void displayQuestion(String ip){
        try {
            URL url = new URL("http://"+ip+"/skillstest42/db/getall.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            InputStream is = conn.getInputStream();
            StringBuffer data = new StringBuffer();
            int ch=0;
            while ((ch=is.read())!=-1){
                data.append((char)ch);
            }

            is.close();
            conn.disconnect();

//                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    String data = br.readLine();
//                    conn.disconnect();

            JSONObject json = new JSONObject(data.toString());
            JSONArray jarray = json.getJSONArray("questions");
            for (int i = 0; i < jarray.length(); i++){
                JSONObject question = jarray.getJSONObject(i);

                String qno = question.getString("questionno");
                String myquestion = question.getString("question");
                String option1 = question.getString("option1");
                String option2 = question.getString("option2");
                String option3 = question.getString("option3");
                String option4 = question.getString("option4");
                QuestionItem q = new QuestionItem(qno, myquestion, option1, option2, option3, option4, "");
                arrayList.add(q);
                adapter.notifyDataSetChanged();

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void localhostDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_localhost, null);
        builder.setView(dialogView);

        final EditText localhost = dialogView.findViewById(R.id.edit_localhost);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyUtility.ipaddress = localhost.getText().toString();

                loginDialog();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.show();
    }

    public void loginDialog(){
        AlertDialog.Builder sbuilder = new AlertDialog.Builder(this);
        LayoutInflater sinflater = this.getLayoutInflater();
        final View studentView = sinflater.inflate(R.layout.dialog_student, null);
        sbuilder.setView(studentView);

        final EditText stud_idno = studentView.findViewById(R.id.edit_idno);
        final EditText stud_passw = studentView.findViewById(R.id.edit_passw);


        sbuilder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new AsyncLogin().execute(stud_idno.getText().toString(), stud_passw.getText().toString());
            }
        });
        sbuilder.show();
    }

    private class AsyncLogin extends AsyncTask<String, String, String>{
        HttpURLConnection conn;
        URL url = null;


        @Override
        protected String doInBackground(String... params) {
            try{
                url = new URL("http://192.168.254.103/skillstest42/loginstudent.php");
            } catch (Exception e) {
                e.printStackTrace();
                return "exception";
            }

            try {
                //setting up the connection
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                //setDoInput and setDoOutput
                conn.setDoInput(true);
                conn.setDoOutput(true);

                //append the params
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("idno", params[0])
                        .appendQueryParameter("passw", params[1]);

                String query = builder.build().getEncodedQuery();

                // open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (Exception e) {
                e.printStackTrace();
                return "exception";
            }

            try {
                int response_code = conn.getResponseCode();

                //check if successful
                if (response_code == HttpURLConnection.HTTP_OK){
                    //read data
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null){
                        result.append(line);
                    }

                    //pass data
                    return (result.toString());
                } else{
                    return ("unsuccessful");
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equalsIgnoreCase("true")){
                displayQuestion(MyUtility.ipaddress);
                Toast.makeText(MainActivity.this, "Successfully logged in.", Toast.LENGTH_SHORT).show();

            } else if (result.equalsIgnoreCase("false")){
                Toast.makeText(MainActivity.this, "Invalid email/password!", Toast.LENGTH_SHORT).show();
            } else if (result.equalsIgnoreCase("exception")){
                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }



            super.onPostExecute(result);
        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String q = this.arrayList.get(position).getQuestionno()+"\n"+this.arrayList.get(position).getQuestion()+"\n"+this.arrayList.get(position).getOption1()+"\n"+this.arrayList.get(position).getOption2()+"\n"+this.arrayList.get(position).getOption3()+"\n"+this.arrayList.get(position).getOption4();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        LayoutInflater qinflater = this.getLayoutInflater();
//        final View questionview = qinflater.inflate(R.layout.dialog_question, null);
//
//        final TextView qno = (TextView) questionview.findViewById(R.id.question_no);
//        final TextView ques = (TextView) questionview.findViewById(R.id.question);
//        final TextView opt1 = (TextView) questionview.findViewById(R.id.opt1);
//        final TextView opt2 = (TextView) questionview.findViewById(R.id.opt2);
//        final TextView opt3 = (TextView) questionview.findViewById(R.id.opt3);
//        final TextView opt4 = (TextView) questionview.findViewById(R.id.opt4);
//
//        qno.setText(this.arrayList.get(position).getQuestionno());
//        ques.setText(this.arrayList.get(position).getQuestion());
//        opt1.setText(this.arrayList.get(position).getOption1());
//        opt2.setText(this.arrayList.get(position).getOption2());
//        opt3.setText(this.arrayList.get(position).getOption3());
//        opt4.setText(this.arrayList.get(position).getOption4());


        TextView textView = new TextView(this);
        textView.setText(q);
        builder.setView(textView);


        builder.setPositiveButton("Okay", null);
        builder.show();
    }

}






