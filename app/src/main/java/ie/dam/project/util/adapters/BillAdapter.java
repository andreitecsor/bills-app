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
import ie.dam.project.data.domain.BillShownInfo;
import ie.dam.project.util.converters.DateConverter;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
    private List<BillShownInfo> billShownInfos;
    private RecyclerViewItemClick recyclerViewItemClick;

    public BillAdapter(List<BillShownInfo> billShownInfos, RecyclerViewItemClick recyclerViewItemClick) {
        this.billShownInfos = billShownInfos;
        this.recyclerViewItemClick = recyclerViewItemClick;
    }

    @NonNull
    @Override
    public BillAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_view_bill, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BillAdapter.ViewHolder holder, int position) {
        holder.tvSupplierName.setText(billShownInfos.get(position).getName());
        holder.tvDueTo.setText(DateConverter.toString(billShownInfos.get(position).getBill().getDueTo()));
        if (billShownInfos.get(position).getBill().isRecurrent()) {
            holder.ivRecurrent.setVisibility(View.VISIBLE);
        } else {
            holder.ivRecurrent.setVisibility(View.INVISIBLE);
        }
        if (billShownInfos.get(position).getBill().isPaid()) {
            holder.ivChecked.setVisibility(View.VISIBLE);
            holder.ivNotPaid.setVisibility(View.INVISIBLE);
        } else {
            holder.ivChecked.setVisibility(View.INVISIBLE);
            holder.ivNotPaid.setVisibility(View.VISIBLE);
        }
        holder.tvType.setText(billShownInfos.get(position).getBill().getType());
        holder.tvAmount.setText(billShownInfos.get(position).getBill().getAmount() + "EUR");
    }

    @Override
    public int getItemCount() {
        return billShownInfos.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPayment;
        private ImageView ivRecurrent;
        private ImageView ivChecked;
        private ImageView ivNotPaid;
        private TextView tvSupplierName;
        private TextView tvDueTo;
        private TextView tvType;
        private TextView tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initialiseComponents(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewItemClick != null) {
                        recyclerViewItemClick.onItemClickListener(getAdapterPosition());
                    }
                }
            });
        }

        private void initialiseComponents(@NonNull View itemView) {
            ivPayment = itemView.findViewById(R.id.rv_iv_payment);
            ivRecurrent = itemView.findViewById(R.id.iv_recurrent);
            ivChecked = itemView.findViewById(R.id.rv_iv_checked);
            ivNotPaid = itemView.findViewById(R.id.rv_iv_not_paid);
            tvSupplierName = itemView.findViewById(R.id.rv_tv_supplier);
            tvDueTo = itemView.findViewById(R.id.rv_tv_due_to);
            tvType = itemView.findViewById(R.id.rv_bill_tv_type);
            tvAmount = itemView.findViewById(R.id.rv_tv_amount);
        }


    }
}
