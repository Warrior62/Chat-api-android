package com.example.tp2_chat_api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {

        @GET("conversations")
        Call<ListConversations> doGetListConversation(@Header("hash") String hash);

        @GET("conversations/{id}/messages")
        Call<ListMessages> doGetListMessage(@Header("hash") String hash, @Path("id") String idConv);

        @FormUrlEncoded
        @POST("conversations/{id}/messages")
        Call<ResponseBody> doPostMessage(@Header("hash") String hash, @Path("id") String idConv, @Field("contenu") String message);
}
