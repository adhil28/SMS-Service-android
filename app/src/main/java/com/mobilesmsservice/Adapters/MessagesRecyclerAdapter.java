package com.mobilesmsservice.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobilesmsservice.R;

import org.json.JSONArray;
import org.json.JSONException;

public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessagesRecyclerAdapter.ViewHolder> {

    JSONArray messages;
    private static final String TAG = "MessagesRecyclerAdapter";
    int count;

    public MessagesRecyclerAdapter(JSONArray messages, int count) {
        this.messages = messages;
        this.count = count;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_rv_layout,null,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.phone.setText("Phone : "+messages.getJSONObject(messages.length()-position-1).getString("phone"));
            holder.message.setText("Message : "+messages.getJSONObject(messages.length()-position-1).getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        Log.e(TAG, "getItemCount: "+count);
        if (count<11){
            Log.e(TAG, "getItemCount: less than 11" );
            return count;
        }else{
            Log.e(TAG, "getItemCount: more than 11" );
            return 10;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message,phone;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message_tv);
            phone = itemView.findViewById(R.id.phone_tv);
        }
    }
}
