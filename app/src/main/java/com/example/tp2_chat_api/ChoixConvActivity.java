package com.example.tp2_chat_api;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tp2_chat_api.GlobalState;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChoixConvActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    GlobalState gs;
    ChoixConvActivity cca = this;
    TextView textViewMsg;
    Button btnChoixConv;
    int indexConv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_conversation);
        gs = (GlobalState) getApplication();
        Bundle bdl = this.getIntent().getExtras();
        gs.alerter("hash : " + bdl.getString("hash"));

        textViewMsg = findViewById(R.id.textViewMsg);
        btnChoixConv = findViewById(R.id.btnChoixConv);

        APIInterface apiService = APIClient.getClient().create(APIInterface.class);
        Call<ListConversations> callGetConvs = apiService.doGetListConversation(bdl.getString("hash"));
        callGetConvs.enqueue(new Callback<ListConversations>() {
            @Override
            public void onResponse(Call<ListConversations> call, Response<ListConversations> response) {
                ListConversations lc = response.body();
                ArrayList<Conversation> listeConv = lc.getConversations();
                Log.v("ChoixConv", listeConv.toString());
                List<String> lcStr = new ArrayList<>();
                ArrayList<String> lcId = new ArrayList<>();
                for(Conversation c : listeConv) {
                    lcStr.add(c.getTheme());
                    lcId.add(c.getId());
                }
                Spinner spinner = (Spinner) findViewById(R.id.spinConversations);
                spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) cca);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(cca, android.R.layout.simple_spinner_item, lcStr);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);


                btnChoixConv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textViewMsg.setText("");
                        String idConv = lcId.get(indexConv);
                        Call<ListMessages> callGetMsg = apiService.doGetListMessage(bdl.getString("hash"), idConv);
                        callGetMsg.enqueue(new Callback<ListMessages>() {
                            @Override
                            public void onResponse(Call<ListMessages> call, Response<ListMessages> response) {
                                ListMessages lm = response.body();
                                ArrayList<Message> listeMsg = lm.getMessages();
                                Log.v("ChoixConv", "idConv="+idConv+", indexConv="+indexConv);
                                Log.v("ChoixConv", listeMsg.toString());
                                List<String> lmStr = new ArrayList<>();
                                HashMap<String, String> lmMap = new HashMap<>();
                                for(Message m : listeMsg) {
                                    lmStr.add(m.getContenu());
                                    lmMap.put(m.getId(), m.getContenu());
                                    textViewMsg.append(m.getAuteur() + " : " + m.getContenu() + "\n");
                                }
                                textViewMsg.setMovementMethod(new ScrollingMovementMethod());
                            }

                            @Override
                            public void onFailure(Call<ListMessages> call, Throwable t) {

                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(Call<ListConversations> call, Throwable t) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // On selecting a spinner item
        String item = adapterView.getItemAtPosition(i).toString();
        indexConv = i;
        // Showing selected spinner item
        Toast.makeText(adapterView.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
