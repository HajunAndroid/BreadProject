package kr.co.hajun.breadproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Callback;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {
    private List<Row> items; //각 제과점 정보를 담고 있는 list입니다.
    public Context mContext;

    public StoreAdapter(Context context, List<Row> items){
        this.items=items;
        this.mContext = context;
    }

    @NonNull
    @Override
    public StoreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item , parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Row item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView storeName, storeAddress;
        public ViewHolder(View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.breadStoreName);
            storeAddress = itemView.findViewById(R.id.breadAddress);
            //Adapter 항목을 클릭 시 해당 항목의 제과점 이름이 담긴 Intent가 발생하고
            //MainActivity2 activity가 화면에 보이게 됩니다.
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        Intent intent = new Intent(mContext,MainActivity2.class);
                        intent.putExtra("position",items.get(pos).getBplcnm());
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        public void setItem(Row item){
            storeName.setText(item.getBplcnm());
            storeAddress.setText(item.getSitewhladdr());
        }
    }
}
