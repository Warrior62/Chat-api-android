package com.example.tp2_chat_api;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tp2_chat_api.GlobalState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChoixConvActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    GlobalState gs;
    ChoixConvActivity cca = this;
    TextView textViewMsg;
    Spinner spinner;
    Button btnChoixConv;
    EditText inputMsg;
    AppCompatImageButton btnSendMsg;
    int indexConv;
    String hash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_conversation);
        gs = (GlobalState) getApplication();
        Bundle bdl = this.getIntent().getExtras();

        // init HTML elements by id
        textViewMsg = findViewById(R.id.textViewMsg);
        btnChoixConv = findViewById(R.id.btnChoixConv);
        spinner = findViewById(R.id.spinConversations);
        inputMsg = findViewById(R.id.textMsg);
        btnSendMsg = findViewById(R.id.imageButton);

        // retrieve hash from login bundle
        hash = bdl.getString("hash");

        // GET ListConversations
        APIInterface apiService = APIClient.getClient().create(APIInterface.class);
        Call<ListConversations> callGetConvs = apiService.doGetListConversation(hash);
        callGetConvs.enqueue(new Callback<ListConversations>() {
            @Override
            public void onResponse(Call<ListConversations> call, Response<ListConversations> response) {
                ListConversations lc = response.body();
                if(lc != null){
                    ArrayList<Conversation> listeConv = lc.getConversations();
                    List<String> lcStr = new ArrayList<>();
                    ArrayList<String> lcId = new ArrayList<>();
                    for(Conversation c : listeConv) {
                        lcStr.add(c.getTheme());
                        lcId.add(c.getId());
                    }

                    // Fill up the spinner with all the conversations theme
                    initSpinner(lcStr);

                    // GET ListMessages
                    btnChoixConv.setOnClickListener(view -> {
                        textViewMsg.setText("");
                        String idConv = lcId.get(indexConv);
                        Call<ListMessages> callGetMsg = apiService.doGetListMessage(hash, idConv);
                        callGetMsg.enqueue(new Callback<ListMessages>() {
                            @Override
                            public void onResponse(Call<ListMessages> call1, Response<ListMessages> response1) {
                                updateViewMsg(response1, idConv);

                                btnSendMsg.setOnClickListener(view -> {
                                    ChoixConvActivity.PostAsyncTask reqPOST= new PostAsyncTask();
                                    reqPOST.execute("http://tomnab.fr/chat-api/conversations/"+idConv+"/messages?contenu="+inputMsg.getText().toString(), "");
                                    Call<ResponseBody> callPostMsg = apiService.doPostMessage(hash, idConv, inputMsg.getText().toString());
                                    callPostMsg.enqueue(new Callback() {
                                        @Override
                                        public void onResponse(Call call, Response response) {
                                            gs.alerter("Message sent !");
                                            Call<ListMessages> callGetMsg = apiService.doGetListMessage(hash, idConv);
                                            callGetMsg.enqueue(new Callback<ListMessages>() {
                                                @Override
                                                public void onResponse(Call<ListMessages> call, Response<ListMessages> response) {
                                                    updateViewMsg(response, idConv);
                                                }

                                                @Override
                                                public void onFailure(Call<ListMessages> call, Throwable t) {}
                                            });
                                        }

                                        @Override
                                        public void onFailure(Call call, Throwable t) {}
                                    });
                                });
                            }

                            @Override
                            public void onFailure(Call<ListMessages> call1, Throwable t) {}
                        });
                    });
                }
            }

            @Override
            public void onFailure(Call<ListConversations> call, Throwable t) {}
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // On selecting a spinner item
        indexConv = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    class PostAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... data) {
            return gs.requetePOST(data[0] ,data[1]);
        }

        protected void onPostExecute(String hash) {}
    }

    public void initSpinner(List<String> lcStr){
        spinner.setOnItemSelectedListener(cca);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(cca, android.R.layout.simple_spinner_item, lcStr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void updateViewMsg(Response<ListMessages> response, String idConv){
        ListMessages lm = response.body();
        ArrayList<Message> listeMsg = lm.getMessages();
        Log.v("ChoixConv", "idConv="+idConv+", indexConv="+indexConv);
        String toDisplay = "";
        ArrayList<String> auteurList = new ArrayList<>();
        for(Message m : listeMsg) {
            toDisplay += (m.getAuteur() + " : " + m.getContenu() + "\n");
            auteurList.add(m.getAuteur());
        }
        Set<String> auteurs = new LinkedHashSet<>(auteurList);
        SpannableString spannableString = new SpannableString(toDisplay);
        splitStringByWords(toDisplay, spannableString, auteurs);
        textViewMsg.setText(spannableString);
        textViewMsg.setMovementMethod(new ScrollingMovementMethod());
        textViewMsg.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public HashMap<Integer, Integer> findIndexes(String searchString, String keyword) {
        try {
            String regex = "\\b" + keyword + "\\b";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(searchString);

            HashMap<Integer, Integer> wrappers = new HashMap<>();

            while (matcher.find()) {
                int end = matcher.end();
                int start = matcher.start();
                wrappers.put(start, end);
            }
            return wrappers;
        } catch(Exception e){
            Toast.makeText(cca, "Problem with regex", Toast.LENGTH_LONG);
            return new HashMap<>();
        }
    }

    public void splitStringByWords(String str, SpannableString sstr, Set<String> auteurs){

        String[] words = str.split(" |\n");
        ArrayList<String> wordsList = new ArrayList<>();
        for(String s : words)
            wordsList.add(s);
        for(int i=0; i<wordsList.size(); i++)
            if(wordsList.get(i).equals(":"))
                wordsList.remove(i);

        // Create clickable words
        for(String s : wordsList){
            boolean isAuteur = false;
            // find indexes of s
            HashMap<Integer, Integer> matches = findIndexes(str, s);
            for(String auteur : auteurs)
                if(s.equals(auteur))
                    isAuteur = true;
            if(!isAuteur){
                for(Map.Entry<Integer, Integer> entry: matches.entrySet()){
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setUnderlineText(false);
                        }
                        @Override
                        public void onClick(View widget) {
                            JSONAsyncTask reqGET = new JSONAsyncTask();
                            reqGET.execute("https://api.dictionaryapi.dev/api/v2/entries/en/"+s, "");
                        }
                    };
                    int start = entry.getKey();
                    int end = entry.getValue();
                    sstr.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    private void getDefinitions(String res) throws JSONException {
        ArrayList<String> defs = new ArrayList<>();
        JSONArray array = new JSONArray(res);
        JSONObject obj = array.getJSONObject(0);
        // get all the meanings of the word searched
        JSONArray meanings = obj.getJSONArray("meanings");
        for(int i=0; i<meanings.length(); i++){
            JSONArray definitions = meanings.getJSONObject(i).getJSONArray("definitions");
            for(int j=0; j<definitions.length(); j++){
                String def = (String) definitions.getJSONObject(j).get("definition");
                defs.add("* " + def);
            }
        }

        // create the bundle to display all the meanings into the Definition Activity
        String word = obj.getString("word");
        Bundle bdl = new Bundle();
        bdl.putStringArrayList("defs", defs);
        bdl.putString("word", word);

        Intent toDefinition = new Intent(this, Definition.class);
        toDefinition.putExtras(bdl);
        startActivity(toDefinition);
    }

    class JSONAsyncTask extends AsyncTask<String, Void, String> {
        // Params, Progress, Result

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(gs.CAT,"onPreExecute");
        }

        @Override
        protected String doInBackground(String... data) {
            // pas d'interaction avec l'UI Thread ici
            // data[0] contient le premier arg passé execute()
            // data[1] contient le second arg passé execute()

            String res = gs.requeteGET(data[0], data[1]);
            try {
                getDefinitions(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return res;
        }

        protected void onPostExecute(String result) {
            Log.i(gs.CAT,"onPostExecute");
            Log.d("ChoixConv", result);
            if(result == null || result.equals(""))
                gs.alerter("Unknown or non-english");
        }
    }
}
