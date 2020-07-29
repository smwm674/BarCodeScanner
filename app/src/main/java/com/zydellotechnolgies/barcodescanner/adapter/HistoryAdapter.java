package com.zydellotechnolgies.barcodescanner.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zydellotechnolgies.barcodescanner.MainActivity;
import com.zydellotechnolgies.barcodescanner.R;
import com.zydellotechnolgies.barcodescanner.model.ScanItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ezvcard.Ezvcard;
import ezvcard.VCard;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private List<ScanItem> List;

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


    public HistoryAdapter(List<ScanItem> List) {
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
        ScanItem list = List.get(position);
        holder.date.setText(list.getDay() + " " + list.getTime());
        // holder.text.setText(list.getScanned_item());

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
        {
            String name = null, phone = null, email = null, address = null;
            try {
                address = list.getScanned_item().substring(list.getScanned_item().indexOf(";ADR:") + 5, list.getScanned_item().length());
                holder.text.setText(address);
            } catch (Exception e) {
            }
            try {
                email = list.getScanned_item().substring(list.getScanned_item().indexOf(";EMAIL:") + 7, list.getScanned_item().indexOf(";", list.getScanned_item().indexOf("EMAIL:") + 7));
                holder.text.setText(email);
            } catch (Exception e) {
            }
            try {
                phone = list.getScanned_item().substring(list.getScanned_item().indexOf(";TEL:") + 5, list.getScanned_item().indexOf(";", list.getScanned_item().indexOf("TEL:") + 4));
                holder.text.setText(phone);
            } catch (Exception e) {
            }
            try {
                name = list.getScanned_item().substring(list.getScanned_item().indexOf(":N:") + 3, list.getScanned_item().indexOf(";", list.getScanned_item().indexOf(":N:") + 4));
                holder.text.setText(name
                );
            } catch (Exception e) {
            }
            //   holder.text.setText(list.getScanned_item().substring(list.getScanned_item().indexOf("MECARD:N:") + 9, list.getScanned_item().indexOf(";TEL:")));
        } else if (list.getScanned_item().contains("BEGIN:VCARD")) {
            String name = null, phone = null, email = null, address = null;
            VCard vcard = Ezvcard.parse(list.getScanned_item()).first();
            try {
                address = vcard.getAddresses().get(0).getStreetAddress().toString() + vcard.getAddresses().get(0).getCountry().toString();
                holder.text.setText(address);
            } catch (Exception e) {
            }
            try {
                email = vcard.getEmails().get(0).getValue().toString();
                holder.text.setText(email);
            } catch (Exception e) {
            }
            try {
                phone = vcard.getTelephoneNumbers().get(0).getText().toString();
                holder.text.setText(phone);
            } catch (Exception e) {
            }
            try {
                name = vcard.getFormattedName().getValue().toString();
                holder.text.setText(name);
            } catch (Exception e) {
            }
        } else if (list.getScanned_item().contains("tel")) //telephone
            holder.text.setText(list.getScanned_item().substring(list.getScanned_item().indexOf("tel:") + 4, list.getScanned_item().length()));
        else if (list.getScanned_item().contains("WIFI:"))//wifi
            holder.text.setText(list.getScanned_item().substring(list.getScanned_item().indexOf("S:") + 2, list.getScanned_item().indexOf(";", list.getScanned_item().indexOf("S:") + 3)));
        else if (list.getScanned_item().contains("SMSTO")) //message
            holder.text.setText(list.getScanned_item().substring(list.getScanned_item().indexOf("SMSTO:") + 6, list.getScanned_item().indexOf(":", 7)));
        else// text
            holder.text.setText(list.getScanned_item());

    }

    @Override
    public int getItemCount() {
        return List.size();
    }
}