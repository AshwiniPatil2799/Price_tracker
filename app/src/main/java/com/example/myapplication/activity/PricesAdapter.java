package com.example.myapplication.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class PricesAdapter extends RecyclerView.Adapter<PricesAdapter.PriceViewHolder> {
    private List<PriceTrackerEntity> priceList = new ArrayList<>();

    public void updatePrices(List<PriceTrackerEntity> prices) {
        this.priceList = prices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_price, parent, false);
        return new PriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder holder, int position) {
        holder.bind(priceList.get(position));
    }

    @Override
    public int getItemCount() {
        return priceList.size();
    }

    static class PriceViewHolder extends RecyclerView.ViewHolder {
        private TextView priceName, priceValue,priceChange;

        public PriceViewHolder(View itemView) {
            super(itemView);
            priceName = itemView.findViewById(R.id.descriptionTextView);
            priceValue = itemView.findViewById(R.id.amountTextView);
            priceChange=itemView.findViewById(R.id.categoryTextView);
        }

        public void bind(PriceTrackerEntity price) {
            priceName.setText(price.getName());
            priceValue.setText(String.valueOf(price.getPrice()));
            priceChange.setText(String.valueOf(price.getChange()));
        }
    }

    public void filterList(ArrayList<PriceTrackerEntity> filterlist) {
        priceList = filterlist;
        // below line is to notify our adapter
        notifyDataSetChanged();
    }

}


