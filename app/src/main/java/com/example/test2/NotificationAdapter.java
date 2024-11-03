package com.example.test2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Map<String, Object>> notificationList;

    public NotificationAdapter(List<Map<String, Object>> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Map<String, Object> notification = notificationList.get(position);

        // 알림 메시지를 설정
        String message = (String) notification.get("message");
        holder.notificationText.setText(message);

        // timestamp가 Long 타입이므로 String으로 변환하여 설정
        Long timestampLong = (Long) notification.get("timestamp");
        if (timestampLong != null) {
            String timestamp = String.valueOf(timestampLong);
            holder.notificationTimestamp.setText(timestamp);
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationText;
        TextView notificationTimestamp;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationText = itemView.findViewById(R.id.notification_text);
            notificationTimestamp = itemView.findViewById(R.id.notification_timestamp);
        }
    }
}
