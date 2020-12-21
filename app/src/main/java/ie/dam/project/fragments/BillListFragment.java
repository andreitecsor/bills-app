package ie.dam.project.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.List;

import ie.dam.project.R;
import ie.dam.project.data.domain.Bill;
import ie.dam.project.util.adapters.BillAdapter;

public class BillListFragment extends Fragment {
    public static final String ALL_BILLS = "ALL_BILLS";
    private List<Bill> billList;

    RecyclerView recyclerView;
    BillAdapter billAdapter;

    public BillListFragment() {
    }

    public static BillListFragment newInstance(List<Bill> bills) {
        BillListFragment fragment = new BillListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ALL_BILLS, (Serializable) bills);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            billList = (List<Bill>) getArguments().get(ALL_BILLS);
            System.out.println("FRAG" + billList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_bill_list, container, false);
        initialiseComponents(view);
        return view;
    }

    private void initialiseComponents(View view) {
        recyclerView = view.findViewById(R.id.frg_bill_list_rv);
        billAdapter = new BillAdapter(billList);
        recyclerView.setAdapter(billAdapter);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(dividerItemDecoration);
    }
}