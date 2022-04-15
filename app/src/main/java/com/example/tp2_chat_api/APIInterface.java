package com.example.tp2_chat_api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface APIInterface {

        @GET("conversations")
        Call<ListConversations> doGetListConversation(@Header("hash") String hash);

        @GET("conversations/{id}/messages")
        Call<ListMessages> doGetListMessage(@Header("hash") String hash, @Path("id") String idConv);
}
