package com.example.skillstest42;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
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
        listView = (ListView) findViewById(R.id.listview_questions);
        adapter = new ArrayAdapter<QuestionItem>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();

        localhostDialog(); //

    }

    public void localhostDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_localhost, null);
        builder.setView(dialogView);

        final EditText localhost = (EditText) dialogView.findViewById(R.id.edit_localhost);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyUtility.ipaddress = localhost.getText().toString();

                try {
                    URL url = new URL("http://"+MyUtility.ipaddress+"/skillstest42/db/getall.php");
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
//                    Toast.makeText(MainActivity.this, data+"", Toast.LENGTH_SHORT).show();

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

//                try {
//                    URL url = new URL("http://"+MyUtility.ipaddress+"/skillstest42/db/getall.php");
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    String data = br.readLine();
//                    Toast.makeText(MainActivity.this, data+" is the data", Toast.LENGTH_SHORT).show();
//                    conn.disconnect();
//                    //
//                    //processing the json formatted data
//                    JSONObject json = new JSONObject(data);
//                    JSONArray jarray = json.getJSONArray("questions");
//
//                    Toast.makeText(MainActivity.this, jarray.length()+" is the length", Toast.LENGTH_SHORT).show();
//
////                        for (int i = 0; i < jarray.length(); i++){
////                            JSONObject question = jarray.getJSONObject(i);
////
////                            String qno = question.getString("questionno");
////                            String myquestion = question.getString("question");
////                            String option1 = question.getString("option1");
////                            String option2 = question.getString("option2");
////                            String option3 = question.getString("option3");
////                            String option4 = question.getString("option4");
////                            QuestionItem q = new QuestionItem(qno, myquestion, option1, option2, option3, option4, "");
////
////                            Toast.makeText(MainActivity.this, qno+"\n"+myquestion+"\n"+option1, Toast.LENGTH_SHORT).show();
////                            arrayList.add(q);
////                        }
////                        adapter.notifyDataSetChanged();
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

//                loginDialog();
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

        final EditText stud_idno = (EditText) studentView.findViewById(R.id.edit_idno);
        final EditText stud_passw = (EditText) studentView.findViewById(R.id.edit_passw);

        sbuilder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        sbuilder.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String q = this.arrayList.get(position).getQuestion()+"\n"+this.arrayList.get(position).getOption1()+"\n"+this.arrayList.get(position).getOption2()+"\n"+this.arrayList.get(position).getOption3()+"\n"+this.arrayList.get(position).getOption4();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView textView = new TextView(this);
        textView.setText(q);
        builder.setView(textView);
        builder.setPositiveButton("Okay", null);
        builder.show();
    }
}
