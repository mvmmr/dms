package com.mad.dms.orders;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mad.dms.R;
import com.mad.dms.utils.RecyclerTouchListener;
import com.mad.dms.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;

//public class OrderMainActivity extends AppCompatActivity implements OrderMainAdapter.ItemClickListener {
public class OrderMainActivity extends AppCompatActivity {

    public static final int VIEW_ORDER_ACTIVITY_REQUEST_CODE = 1;
    public static final String VIEW_ORDER_EXTRA = "com.mad.dms.orders.VIEW_ORDER_EXTRA";
    public static final String VIEW_ORDER_POS = "com.mad.dms.orders.VIEW_ORDER_POS";
    ArrayList<Order> orders;
    OrderMainAdapter adapter;
    OrderDBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(OrderMainActivity.this, OrderAddActivity.class);
//                startActivityForResult(intent, NEW_ORDER_ACTIVITY_REQUEST_CODE);
                showOrderDialog();
            }
        });

        db = new OrderDBHelper(this);
        orders = new ArrayList<>();
        orders.addAll(db.getAllOrders());
//        orders.add(new Order("Order 1", Order.ORDER_STATUS_CONFIRMED, new Date()));
//        orders.add(new Order("Order 2", Order.ORDER_STATUS_PENDING));
//        orders.add(new Order("Order 3", Order.ORDER_STATUS_DENIED));

        RecyclerView recyclerView = findViewById(R.id.rvOrderMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                clickItem(position);
            }
            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));

        adapter = new OrderMainAdapter(this, orders);
//        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void createOrder(String name) {
        long id = db.insertOrder(name);
        Order o = db.getOrder(id);
        if (o != null) {
            orders.add(0, o);
            adapter.notifyDataSetChanged();
        }
    }

    private void showOrderDialog() {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.order_add_dialog, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(OrderMainActivity.this);
        dialogBuilder.setView(view);
        dialogBuilder
                .setCancelable(true)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogBox, int i) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogBox, int i) {
                        dialogBox.cancel();
                    }
                });

        final AlertDialog alertDialog = dialogBuilder.create();
        final EditText orderInput = view.findViewById(R.id.order_add_note_input);

        alertDialog.show();
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(orderInput.getText().toString())) {
                    Toast.makeText(OrderMainActivity.this, "Enter a name!", Toast.LENGTH_SHORT).show();
                } else {
                    createOrder(orderInput.getText().toString());
                    alertDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIEW_ORDER_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
//            int index = 0;
//            int orderId = data.getIntExtra(OrderAddActivity.EXTRA_REPLY, -1);
//            if (orderId > 0) {
//                orders.add(index, db.getOrder(orderId));
//                adapter.notifyItemInserted(index);
//            }
//            int orderId = data.getIntExtra(OrderViewActivity.VIEW_ORDER_REPLY_ID, 0);
            int orderPos = data.getIntExtra(OrderViewActivity.VIEW_ORDER_REPLY_POS, 0);

            Order o = db.getOrder(orders.get(orderPos).getId());
            if (o != null) {
                orders.set(orderPos, o);
                adapter.notifyDataSetChanged();
            }

        }
    }

//    @Override
//    public void onItemClick(View view, int position) {
////        String toastText = "You clicked" + adapter.getItem(position).getName() + " on row number " + position;
////        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(OrderMainActivity.this, OrderViewActivity.class);
//        intent.putExtra(VIEW_ORDER_EXTRA, adapter.getItem(position).getId());
//        intent.putExtra(VIEW_ORDER_POS, position);
//        startActivityForResult(intent, VIEW_ORDER_ACTIVITY_REQUEST_CODE);
//    }

    private void clickItem(final int position) {
        Intent intent = new Intent(OrderMainActivity.this, OrderViewActivity.class);
        intent.putExtra(VIEW_ORDER_EXTRA, adapter.getItem(position).getId());
        intent.putExtra(VIEW_ORDER_POS, position);
        startActivityForResult(intent, VIEW_ORDER_ACTIVITY_REQUEST_CODE);
    }

    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    deleteOrder(position);
                }
            }
        });
        builder.show();
    }

    private void deleteOrder(int position) {
        db.deleteOrder(orders.get(position));
        orders.remove(position);
        adapter.notifyItemRemoved(position);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_order_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}