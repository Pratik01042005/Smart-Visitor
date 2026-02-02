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
import com.example.meetpoint.VisitorDetailActivity;
import com.example.meetpoint.models.VisitorModel;

import java.util.ArrayList;

public class VisitorAdapter extends RecyclerView.Adapter<VisitorAdapter.ViewHolder> {

    Context context;
    ArrayList<VisitorModel> list;

    public VisitorAdapter(Context context, ArrayList<VisitorModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.row_visitor_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        VisitorModel v = list.get(position);

        h.tvName.setText(v.getName());
        h.tvPurpose.setText(v.getPurpose());
        h.tvDate.setText(v.getVisitDateTime()); // ✅ FIXED (correct field)

        // ================= STATUS =================
        String status = v.getStatus() == null ? "pending" : v.getStatus().toLowerCase();

        if (status.equals("approved")) {
            h.tvStatus.setText("Approved");
            h.tvStatus.setTextColor(context.getColor(R.color.green));

        } else if (status.equals("rejected")) {
            h.tvStatus.setText("Rejected");
            h.tvStatus.setTextColor(context.getColor(R.color.red));

        } else {
            h.tvStatus.setText("Pending");
            h.tvStatus.setTextColor(context.getColor(R.color.yellow));
        }

        // ================= DETAILS =================
        h.btnDetails.setOnClickListener(x -> {
            Intent i = new Intent(context, VisitorDetailActivity.class);
            i.putExtra("visitorId", v.getId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ================= VIEW HOLDER =================
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvDate, tvPurpose, tvStatus;
        ImageView btnDetails;

        public ViewHolder(@NonNull View v) {
            super(v);

            tvName = v.findViewById(R.id.tvVisitorName);
            tvDate = v.findViewById(R.id.tvVisitorDate);
            tvPurpose = v.findViewById(R.id.tvVisitorPurpose);
            tvStatus = v.findViewById(R.id.tvVisitorStatus);
            btnDetails = v.findViewById(R.id.btnViewDetails);
        }
    }
}
