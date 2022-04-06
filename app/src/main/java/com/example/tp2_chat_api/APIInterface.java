package com.example.chat_2022_eleves;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface APIInterface {

        @GET("conversations")
        Call<ListConversations> doGetListConversation(@Header("hash") String hash);
}
