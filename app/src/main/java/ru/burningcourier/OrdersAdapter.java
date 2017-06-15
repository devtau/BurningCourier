package ru.burningcourier;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import ru.burningcourier.utils.AppUtils;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {

	private List<Order> data;
	private ProductsAdapterListener listener;


	public OrdersAdapter(List<Order> data, ProductsAdapterListener listener) {
		this.data = data;
		this.listener = listener;
	}


	@Override
	public OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_orders, parent, false);
		return new OrdersViewHolder(view);
	}

	@Override
	public void onBindViewHolder(OrdersViewHolder holder, int position) {
		Order order = data.get(position);
		int rootColorId = order.selected ? R.color.colorGreen : R.color.colorTransparent;
		int rootColor = ContextCompat.getColor(holder.getContext(), rootColorId);
		holder.root.setBackgroundColor(rootColor);
		
		int drawableId = order.delivered ? R.drawable.star_yellow : R.drawable.star_grey;
		Drawable drawable = ContextCompat.getDrawable(holder.getContext(), drawableId);
		holder.itemImg.setImageDrawable(drawable);
		
		holder.orderNum.setText(String.valueOf(order.orderNum));
		holder.address.setText(order.address);
		holder.phone.setText(order.phone);
		holder.note.setText(order.note);
		
		boolean isOrderInRedZone = order.timer <= AppUtils.RED_TIME && !order.delivered;
		int timerTextColorId = isOrderInRedZone ? R.color.colorRed : R.color.colorGreen;
		int timerTextColor = ContextCompat.getColor(holder.getContext(), timerTextColorId);
		holder.timer.setTextColor(timerTextColor);
		holder.timer.setText(AppUtils.formatTimer(order));
		holder.root.setOnClickListener(v -> listener.onOrderSelected(order));
	}

	@Override
	public int getItemCount() {
		return data.size();
	}



	public interface ProductsAdapterListener {
		void onOrderSelected(Order order);
	}
	
	
	
	class OrdersViewHolder extends RecyclerView.ViewHolder {
        
        View root;
        ImageView itemImg;
        TextView orderNum;
        TextView address;
        TextView phone;
        TextView note;
        TextView timer;
    
    
        OrdersViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            itemImg = (ImageView) itemView.findViewById(R.id.itemImg);
            orderNum = (TextView) itemView.findViewById(R.id.orderNum);
            address = (TextView) itemView.findViewById(R.id.address);
            phone = (TextView) itemView.findViewById(R.id.phone);
            note = (TextView) itemView.findViewById(R.id.note);
            timer = (TextView) itemView.findViewById(R.id.timer);
        }
        
        public Context getContext() {
            return root.getContext();
        }
    }
}
