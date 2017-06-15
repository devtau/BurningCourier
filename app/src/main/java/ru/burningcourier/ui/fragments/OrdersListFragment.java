package ru.burningcourier.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import ru.burningcourier.Order;
import ru.burningcourier.R;
import ru.burningcourier.adapters.OrdersAdapter;
import ru.burningcourier.sfClasses.SFApplication;
import ru.burningcourier.ui.MapsActivity;

public class OrdersListFragment extends Fragment {
    
    private static final String LOG_TAG = "OrdersListFragment";
    private OrdersListListener listener;
    private OrdersAdapter adapter;
    private View deliverBtn;
    
    
    public OrdersListFragment() { }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(LOG_TAG, "onAttach");
        if (context instanceof OrdersListListener) {
            listener = (OrdersListListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OrdersListListener");
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        view.findViewById(R.id.updateBtn).setOnClickListener(v -> listener.updateOrders());
        deliverBtn = view.findViewById(R.id.deliverBtn);
        deliverBtn.setOnClickListener(v -> {
            if ((SFApplication.selectedOrder != -1) && !SFApplication.orders.get(SFApplication.selectedOrder).delivered) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.adb_title)
                        .setMessage(R.string.adb_msg)
                        .setPositiveButton(R.string.adb_yes, (dialog, which) -> listener.sendOrder())
                        .setNegativeButton(R.string.adb_no, null)
                        .show();
            }
        });
        initOrdersList(view);
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        if (SFApplication.orders.size() == 0) {
            deliverBtn.setVisibility(View.INVISIBLE);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
    
    private void initOrdersList(View view) {
        adapter = new OrdersAdapter();
        ListView orderList = (ListView) view.findViewById(R.id.orderList);
        orderList.setAdapter(adapter);
        orderList.setOnItemClickListener((adapterView, v, position, id) -> {
            Order selected = SFApplication.orders.get(position);
            MapsActivity.startActivity(getContext(), selected.addressLat, selected.addressLon, "цель");
            
            //TODO: это нам еще пригодится
//            SFApplication.orders.trimToSize();
//            while (position >= SFApplication.orders.size()) {
//                position -= 1;
//            }
//            if (SFApplication.selectedOrder != position) {
//                if (SFApplication.selectedOrder != -1) {
//                    SFApplication.orders.get(SFApplication.selectedOrder).selected = false;
//                }
//                SFApplication.orders.get(position).selected = true;
//                SFApplication.selectedOrder = position;
//            } else {
//                SFApplication.orders.get(SFApplication.selectedOrder).selected = false;
//                SFApplication.selectedOrder = -1;
//            }
//            adapter.notifyDataSetChanged();
            Log.d(LOG_TAG, "list item clicked. Позиция - " + position + ", Id - " + id);
        });
    }
    
    
    
    public interface OrdersListListener {
        void updateOrders();
        void sendOrder();
    }
}
