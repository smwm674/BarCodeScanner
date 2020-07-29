package com.zydellotechnolgies.barcodescanner.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zydellotechnolgies.barcodescanner.MainActivity;
import com.zydellotechnolgies.barcodescanner.R;
import com.zydellotechnolgies.barcodescanner.model.ScanItemCreated;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GeneratedCodeAdapter extends RecyclerView.Adapter<GeneratedCodeAdapter.MyViewHolder> {

    private List<ScanItemCreated> List;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.text)
        TextView text;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public GeneratedCodeAdapter(List<ScanItemCreated> List) {
        this.List = List;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ScanItemCreated list = List.get(position);
        holder.date.setText(list.getDay() + " " + list.getTime());
        // holder.text.setText(list.getScanned_item());
        if (list.getType().equals("BAR_CODE")) {
            Drawable placeholder = holder.image.getContext().getResources().getDrawable(R.drawable.ic_barcode_black);
            holder.image.setImageDrawable(placeholder);
        }
        if (list.getScanned_item().contains("MATMSG")) //email
            holder.text.setText(list.getScanned_item().substring(list.getScanned_item().indexOf("MATMSG:TO:") + 10, list.getScanned_item().indexOf(";SUB:")));
        else if (list.getScanned_item().contains("smsto")) //message
            holder.text.setText(list.getScanned_item().substring(list.getScanned_item().indexOf("smsto:") + 6, list.getScanned_item().indexOf(":", 7)));
        else if (list.getScanned_item().contains("geo")) //location
        {
            if (list.getScanned_item().contains("?q="))
                holder.text.setText(list.getScanned_item().substring(list.getScanned_item().indexOf("geo:") + 4, list.getScanned_item().indexOf("?q=")));
            else
                holder.text.setText(list.getScanned_item().substring(list.getScanned_item().indexOf("geo:") + 4, list.getScanned_item().length()));
        } else if (list.getScanned_item().contains("VEVENT")) //event
            holder.text.setText(list.getScanned_item().substring(list.getScanned_item().indexOf("SUMMARY:") + 8, list.getScanned_item().indexOf("DTSTART")));
        else if (list.getScanned_item().contains("MECARD")) //contact
            holder.text.setText(list.getScanned_item().substring(list.getScanned_item().indexOf("MECARD:N:") + 9, list.getScanned_item().indexOf(";TEL:")));
        else if (list.getScanned_item().contains("tel")) //telephone
            holder.text.setText(list.getScanned_item().substring(list.getScanned_item().indexOf("tel:") + 4, list.getScanned_item().length()));
        else // text
            holder.text.setText(list.getScanned_item());

    }

    @Override
    public int getItemCount() {
        return List.size();
    }
}