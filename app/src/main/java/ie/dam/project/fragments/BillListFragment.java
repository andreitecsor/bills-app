package ie.dam.project.fragments;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ie.dam.project.BillActivity;
import ie.dam.project.R;
import ie.dam.project.data.domain.Bill;
import ie.dam.project.data.domain.BillShownInfo;
import ie.dam.project.data.service.BillService;
import ie.dam.project.util.adapters.BillAdapter;
import ie.dam.project.util.asynctask.Callback;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class BillListFragment extends Fragment {
    public static final String ALL_BILLS = "ALL_BILLS";
    private List<BillShownInfo> billShownInfos = new ArrayList<>();
    private BillService billService;

    private RecyclerView recyclerView;
    private BillAdapter billAdapter;

    public BillListFragment() {
    }

    public static BillListFragment newInstance() {
        BillListFragment fragment = new BillListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        billService = new BillService(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bill_list, container, false);
        initialiseComponents(view);
        return view;
    }

    private void initialiseComponents(View view) {
        recyclerView = view.findViewById(R.id.frg_bill_list_rv);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        billService.getAllWithSupplierName(getAllWithSupplierName());
    }

    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction) {
                case ItemTouchHelper.RIGHT: //LEFT -> RIGHT
                    billService.delete(deleteBill(position), billShownInfos.get(position).getBill());
                    break;

                case ItemTouchHelper.LEFT: //RIGHT <- LEFT
                    Bill billToUpdate = billShownInfos.get(position).getBill();
                    if (billToUpdate.isPayed()) {
                        billToUpdate.setPayed(false);
                    } else {
                        billToUpdate.setPayed(true);
                    }
                    billService.update(updateBill(position), billToUpdate);
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.red))
                    .addSwipeRightActionIcon(R.drawable.ic_delete)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.green))
                    .addSwipeLeftActionIcon(R.drawable.ic_checked)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private Callback<List<BillShownInfo>> getAllWithSupplierName() {
        return new Callback<List<BillShownInfo>>() {
            @Override
            public void runResultOnUiThread(List<BillShownInfo> result) {
                if (result != null) {
                    billShownInfos.clear();
                    billShownInfos.addAll(result);
                    Collections.sort(billShownInfos, new Comparator<BillShownInfo>() {
                        @Override
                        public int compare(BillShownInfo o1, BillShownInfo o2) {
                            int result;
                            result = Boolean.compare(o1.getBill().isPayed(), o2.getBill().isPayed());
                            if (result == 0) {
                                result = o1.getBill().getDueTo().compareTo(o2.getBill().getDueTo());
                            }
                            return result;
                        }
                    });
                    billAdapter = new BillAdapter(billShownInfos);
                    recyclerView.setAdapter(billAdapter);
                }
            }
        };
    }

    private Callback<Bill> updateBill(final int selectedPosition) {
        return new Callback<Bill>() {
            @Override
            public void runResultOnUiThread(Bill result) {
                if (result != null) {
                    billAdapter.notifyItemChanged(selectedPosition);
                }
            }
        };
    }

    private Callback<Integer> deleteBill(final int selectedPosition) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != -1) {
                    billShownInfos.remove(selectedPosition);
                    billAdapter.notifyItemRemoved(selectedPosition);
                }
            }
        };
    }
}