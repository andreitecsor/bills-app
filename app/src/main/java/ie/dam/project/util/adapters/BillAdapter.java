package ie.dam.project.util.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ie.dam.project.R;
import ie.dam.project.data.domain.Bill;
import ie.dam.project.util.converters.DateConverter;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
    private List<Bill> billList;

    public BillAdapter(List<Bill> billList) {
        this.billList = billList;
    }

    @NonNull
    @Override
    public BillAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_view_bill,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BillAdapter.ViewHolder holder, int position) {
        holder.tvSupplierName.setText("SUP");
        holder.tvDueTo.setText(DateConverter.toString(billList.get(position).getDueTo()));
        if(billList.get(position).isRecurrent()){
            holder.ivRecurrent.setVisibility(View.VISIBLE);
        }else{
            holder.ivRecurrent.setVisibility(View.INVISIBLE);
        }
        if(billList.get(position).isPayed()){
            holder.ivChecked.setVisibility(View.VISIBLE);
            holder.ivNotPayed.setVisibility(View.INVISIBLE);
        }else{
            holder.ivChecked.setVisibility(View.INVISIBLE);
            holder.ivNotPayed.setVisibility(View.VISIBLE);
        }
        holder.tvType.setText(billList.get(position).getType());
        holder.tvAmount.setText(String.valueOf(billList.get(position).getAmount())+"€");
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPayment;
        private ImageView ivRecurrent;
        private ImageView ivChecked;
        private ImageView ivNotPayed;
        private TextView tvSupplierName;
        private TextView tvDueTo;
        private TextView tvType;
        private TextView tvAmount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initialiseComponents(itemView);

        }
        private void initialiseComponents(@NonNull View itemView) {
            ivPayment = itemView.findViewById(R.id.rv_iv_payment);
            ivRecurrent = itemView.findViewById(R.id.iv_recurrent);
            ivChecked = itemView.findViewById(R.id.rv_iv_checked);
            ivNotPayed = itemView.findViewById(R.id.rv_iv_not_payed);
            tvSupplierName = itemView.findViewById(R.id.rv_tv_supplier);
            tvDueTo = itemView.findViewById(R.id.rv_tv_due_to);
            tvType = itemView.findViewById(R.id.rv_bill_tv_type);
            tvAmount = itemView.findViewById(R.id.rv_tv_amount);
        }
    }
}
