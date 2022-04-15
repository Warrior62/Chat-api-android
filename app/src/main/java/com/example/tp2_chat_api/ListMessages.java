package com.example.tp2_chat_api;

import java.util.ArrayList;

public class ListMessages {
    String version;
    String success;
    String status;
    ArrayList<Message> messages;



    @Override
    public String toString() {
        return "ListConversations{" +
                "version='" + version + '\'' +
                ", success='" + success + '\'' +
                ", status='" + status + '\'' +
                ", messages=" + messages +
                '}';
    }


    public ArrayList<Message> getMessages() {
        return messages;
    }
}
