package com.example.chat_2022_eleves;

import java.util.ArrayList;

public class ListConversations {
    String version;
    String success;
    String status;
    ArrayList<Conversation> conversations;

    //{"version":1.3,"success":true,"status":200,"conversations"

    @Override
    public String toString() {
        return "ListConversations{" +
                "version='" + version + '\'' +
                ", success='" + success + '\'' +
                ", status='" + status + '\'' +
                ", conversations=" + conversations +
                '}';
    }


    public ArrayList<Conversation> getConversations() {
        return conversations;
    }


}
