package ru.burningcourier.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import ru.burningcourier.R;
import ru.burningcourier.api.model.Order;
import ru.burningcourier.utils.AppUtils;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

	private List<Order> data;
	private AdapterListener listener;


	public OrdersAdapter(List<Order> data, AdapterListener listener) {
		this.data = data;
		this.listener = listener;
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Order order = data.get(position);
		holder.orderId.setText(String.valueOf(order.orderId));
		holder.address.setText(order.address);
		holder.timer.setTextColor(AppUtils.processTimerColor(order, holder.getContext()));
		holder.timer.setText(AppUtils.formatTimer(order));
		holder.root.setOnClickListener(v -> listener.onOrderSelected(order));
	}

	@Override
	public int getItemCount() {
		return data.size();
	}



	public interface AdapterListener {
		void onOrderSelected(Order order);
	}
	
	
	
	class ViewHolder extends RecyclerView.ViewHolder {
        
        View root;
        TextView orderId;
        TextView address;
        TextView timer;
		
		
		ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
			orderId = (TextView) itemView.findViewById(R.id.orderId);
            address = (TextView) itemView.findViewById(R.id.address);
            timer = (TextView) itemView.findViewById(R.id.timer);
        }
        
        public Context getContext() {
            return root.getContext();
        }
    }
}
