package com.example.skillstest42;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class QuestionlistActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView lv;
    private ArrayList<QuestionItem> list = new ArrayList<QuestionItem>();
    private ArrayAdapter<QuestionItem> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionlist);

        lv = (ListView) findViewById(R.id.listviewqlist);

        lv.setOnItemClickListener(this);
        adapter = new ArrayAdapter<QuestionItem>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);


        //
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected void onStart() {
        super.onStart();

        displayQList();

    }

    public void displayQList(){
        try {
            URL url = new URL("http://192.168.254.112/skillstest42/db/getall.php");
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
                list.add(q);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String q_number = this.list.get(position).getQuestionno();
        String q = this.list.get(position).getQuestion();
        String opt_1 = this.list.get(position).getOption1();
        String opt_2 = this.list.get(position).getOption2();
        String opt_3 = this.list.get(position).getOption3();
        String opt_4 = this.list.get(position).getOption4();


        //use intent to display the data to another activity
        Intent intent = new Intent(QuestionlistActivity.this, ViewQuestionActivity.class);
        intent.putExtra("qno", q_number);
        intent.putExtra("question", q);
        intent.putExtra("opt1", opt_1);
        intent.putExtra("opt2", opt_2);
        intent.putExtra("opt3", opt_3);
        intent.putExtra("opt4", opt_4);
        startActivity(intent);
    }
}
