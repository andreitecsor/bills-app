package ie.dam.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ie.dam.project.data.domain.Bill;
import ie.dam.project.data.domain.BillShownInfo;
import ie.dam.project.data.service.BillService;
import ie.dam.project.fragments.RegisterFragment;
import ie.dam.project.util.adapters.BillAdapter;
import ie.dam.project.util.adapters.RecyclerViewItemClick;
import ie.dam.project.util.asynctask.Callback;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class BillActivity extends AppCompatActivity implements RecyclerViewItemClick {
    public static final int ADD_BILL = 101;
    public static final int EDIT_BILL = 102;
    public static final String BILL_TO_UPDATE = "BILL_TO_UPDATE";

    private BillService billService;
    private List<BillShownInfo> billShownInfos = new ArrayList<>();

    private RecyclerView recyclerView;
    private BillAdapter billAdapter;
    private FloatingActionButton fabAddBill;
    private FloatingActionButton fabFilterBill;
    private FloatingActionButton fabExtend;

    private boolean fabExtendClicked = false;
    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;

    private SharedPreferences preferences;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        billService = new BillService(getApplicationContext());
        initialiseComponents();
    }

    private void initialiseComponents() {
        setCurrentDate();
        rotateOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_close);
        fromBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_bot);
        toBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.to_bot);
        fabAddBill = findViewById(R.id.act_bill_fab_add);
        fabFilterBill = findViewById(R.id.act_bill_fab_filter);
        fabExtend = findViewById(R.id.act_bill_fab_extend);
        recyclerView = findViewById(R.id.act_bill_list_rv);
        fabAddBill.setOnClickListener(openAddBillActivity());
        fabFilterBill.setOnClickListener(openFilterActivity());
        fabExtend.setOnClickListener(moreFabs());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        billService.getAllWithSupplierName(getAllBillShownInfos());
        user = FirebaseAuth.getInstance().getCurrentUser();
        preferences = getSharedPreferences(user.getUid() + RegisterFragment.SHARED_PREF_FILE_EXTENSION, MODE_PRIVATE);
    }

    private void setCurrentDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        TextView currentDate = findViewById(R.id.act_bill_tv_date);
        currentDate.setText(formatter.format(date));
    }

    private View.OnClickListener openAddBillActivity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddEditBillActivity.class);
                startActivityForResult(intent, ADD_BILL);
                overridePendingTransition(R.anim.bot_to_top_in, R.anim.bot_to_top_out);
            }
        };
    }

    private View.OnClickListener openFilterActivity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FilterActivity.class));
                finish();
                overridePendingTransition(R.anim.left_to_right_in, R.anim.left_to_right_out);
            }
        };
    }

    private View.OnClickListener moreFabs() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFabsVisibility(fabExtendClicked);
                startFabsAnimation(fabExtendClicked);
                fabExtendClicked = !fabExtendClicked;
            }
        };
    }

    private void startFabsAnimation(boolean fabExtendClicked) {
        if (!fabExtendClicked) {
            fabExtend.startAnimation(rotateOpen);
            fabAddBill.startAnimation(fromBottom);
            fabFilterBill.startAnimation(fromBottom);
        } else {
            fabExtend.startAnimation(rotateClose);
            fabAddBill.startAnimation(toBottom);
            fabFilterBill.startAnimation(toBottom);
        }
    }

    private void setFabsVisibility(boolean fabExtendClicked) {
        if (!fabExtendClicked) {
            fabAddBill.setVisibility(View.VISIBLE);
            fabFilterBill.setVisibility(View.VISIBLE);
        } else {
            fabAddBill.setVisibility(View.INVISIBLE);
            fabFilterBill.setVisibility(View.INVISIBLE);
        }
    }

    private Callback<List<BillShownInfo>> getAllBillShownInfos() {
        return new Callback<List<BillShownInfo>>() {
            @Override
            public void runResultOnUiThread(List<BillShownInfo> result) {
                if (result != null) {
                    billShownInfos.clear();
                    billShownInfos.addAll(result);
                    Collections.sort(billShownInfos, billsComparator());
                    if (billAdapter == null) {
                        billAdapter = new BillAdapter(billShownInfos, BillActivity.this, preferences.getString(PreferenceActivity.CURRENCY_KEY, getString(R.string.default_currency)));
                        recyclerView.setAdapter(billAdapter);
                    } else {
                        billAdapter.notifyDataSetChanged();
                    }
                }
            }
        };
    }

    private Comparator<BillShownInfo> billsComparator() {
        return new Comparator<BillShownInfo>() {
            @Override
            public int compare(BillShownInfo o1, BillShownInfo o2) {
                int result;
                result = Boolean.compare(o1.getBill().isPaid(), o2.getBill().isPaid());
                if (result == 0) {
                    result = o1.getBill().getDueTo().compareTo(o2.getBill().getDueTo());
                }
                return result;
            }
        };
    }

    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromCurrentPosition = viewHolder.getAdapterPosition();
            int toNewPosition = target.getAdapterPosition();
            Collections.swap(billShownInfos, fromCurrentPosition, toNewPosition);
            billAdapter.notifyItemMoved(fromCurrentPosition, toNewPosition);
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
                    if (billToUpdate.isPaid()) {
                        billToUpdate.setPaid(false);
                    } else {
                        billToUpdate.setPaid(true);
                    }
                    billService.update(updatePaidBill(position), billToUpdate);
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red))
                    .addSwipeRightActionIcon(R.drawable.ic_delete)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green))
                    .addSwipeLeftActionIcon(R.drawable.ic_check_mark)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

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

    private Callback<Bill> updatePaidBill(final int selectedPosition) {
        return new Callback<Bill>() {
            @Override
            public void runResultOnUiThread(Bill result) {
                if (result != null) {
                    billAdapter.notifyItemChanged(selectedPosition);
                    billService.getAllWithSupplierName(getAllBillShownInfos());
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Bill bill = (Bill) data.getSerializableExtra(AddEditBillActivity.PROCESSED_BILL);
            if (requestCode == ADD_BILL) {
                billService.insert(insertBill(), bill);
                return;
            }
            if (requestCode == EDIT_BILL) {
                billService.update(updateBill(), bill);
                return;
            }
        }
        billService.getAllWithSupplierName(getAllBillShownInfos());
    }

    private Callback<Bill> insertBill() {
        return new Callback<Bill>() {
            @Override
            public void runResultOnUiThread(Bill result) {
                if (result != null) {
                    billService.getAllWithSupplierName(getAllBillShownInfos());
                }
            }
        };
    }

    private Callback<Bill> updateBill() {
        return new Callback<Bill>() {
            @Override
            public void runResultOnUiThread(Bill result) {
                if (result != null) {
                    billService.getAllWithSupplierName(getAllBillShownInfos());
                }
            }
        };
    }

    @Override
    public void onItemClickListener(int index) {
        Bill billToUpdate = billShownInfos.get(index).getBill();
        Intent intent = new Intent(getApplicationContext(), AddEditBillActivity.class);
        intent.putExtra(BILL_TO_UPDATE, billToUpdate);
        startActivityForResult(intent, EDIT_BILL);
        overridePendingTransition(R.anim.bot_to_top_in, R.anim.bot_to_top_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.bot_to_top_in, R.anim.bot_to_top_out);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setFabsVisibility(fabExtendClicked);
        startFabsAnimation(fabExtendClicked);
        fabExtendClicked = !fabExtendClicked;
    }


}