package ke.co.struct.chauffeurrider.adapters;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ke.co.struct.chauffeurrider.R;
import ke.co.struct.chauffeurrider.activities.HistorySinglePage;
import ke.co.struct.chauffeurrider.objects.HistoryObject;

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.RecyclerViewHolder> {
    private List<HistoryObject> itemList;
    private Context mContext;



    public RideHistoryAdapter(List<HistoryObject> itemList, Context mContext) {
        this.itemList = itemList;
        this.mContext = mContext;
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history,parent,false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.imgConnect.setImageResource(R.mipmap.fromto);
        holder.toHeader.setText(R.string.to);
        holder.fromHeader.setText(R.string.from);
        holder.time.setText(itemList.get(position).getRidetime());
        holder.date.setText(itemList.get(position).getRidedate());
        holder.from.setText(itemList.get(position).getFrom());
        holder.to.setText(itemList.get(position).getTo());
        holder.historyid.setText( itemList.get(position).getHistoryid());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView from,to,date,time,fromHeader,toHeader,historyid;
        ImageView imgConnect;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            from = itemView.findViewById(R.id.from);
            date = itemView.findViewById(R.id.rideDate);
            time = itemView.findViewById(R.id.rideTime);
            to = itemView.findViewById(R.id.to);
            historyid = itemView.findViewById(R.id.historyid);
            toHeader = itemView.findViewById(R.id.toHeader);
            fromHeader = itemView.findViewById(R.id.fromHeader);
            imgConnect = itemView.findViewById(R.id.connector);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), HistorySinglePage.class);
            Bundle b = new Bundle();
            b.putString("historyid", historyid.getText().toString());
            intent.putExtras(b);
            view.getContext().startActivity(intent);
        }
    }
}

