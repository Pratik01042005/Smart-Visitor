package com.example.meetpoint.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetpoint.R;
import com.example.meetpoint.VisitorDetailSAActivity;
import com.example.meetpoint.models.VisitorModel;

import java.util.ArrayList;

public class VisitorAdapterSA extends RecyclerView.Adapter<VisitorAdapterSA.ViewHolder> {

    Context context;
    ArrayList<VisitorModel> list;

    public VisitorAdapterSA(Context context, ArrayList<VisitorModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.row_visitor_item_sa, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        VisitorModel model = list.get(position);

        holder.tvName.setText(model.getName());
        holder.tvPurpose.setText(model.getPurpose());

        // ✅ FIX: use visitDateTime instead of visitDate
        holder.tvDate.setText(model.getVisitDateTime());

        holder.tvStatus.setText(model.getStatus());

        if (model.isApproved()) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_green);
        } else if (model.isRejected()) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_red);
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_yellow);
        }

        holder.btnView.setOnClickListener(v -> {
            Intent i = new Intent(context, VisitorDetailSAActivity.class);
            i.putExtra("visitorId", model.getId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPurpose, tvDate, tvStatus;
        ImageView btnView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvVisitorName);
            tvPurpose = itemView.findViewById(R.id.tvVisitorPurpose);
            tvDate = itemView.findViewById(R.id.tvVisitorDate);
            tvStatus = itemView.findViewById(R.id.tvVisitorStatus);
            btnView = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
