package ru.burningcourier.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;
import ru.burningcourier.R;
import ru.burningcourier.Order;
import ru.burningcourier.sfClasses.SFApplication;
import ru.burningcourier.utils.AppUtils;

public class OrdersAdapter extends BaseAdapter {

    private List<Order> data;

    
    public OrdersAdapter() {
        data = SFApplication.orders;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Order getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_orders, viewGroup, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        Order order = getItem(position);
        int rootColorId = order.selected ? R.color.colorGreen : R.color.colorTransparent;
        int rootColor = ContextCompat.getColor(convertView.getContext(), rootColorId);
        viewHolder.root.setBackgroundColor(rootColor);

        int drawableId = order.delivered ? R.drawable.star_yellow : R.drawable.star_grey;
        Drawable drawable = ContextCompat.getDrawable(convertView.getContext(), drawableId);
        viewHolder.itemImg.setImageDrawable(drawable);

        viewHolder.orderNum.setText(String.valueOf(order.orderNum));
        viewHolder.address.setText(order.address);
        viewHolder.phone.setText(order.phone);
        viewHolder.note.setText(order.note);

        boolean isOrderInRedZone = order.timer <= AppUtils.RED_TIME && !order.delivered;
        int timerTextColorId = isOrderInRedZone ? R.color.colorRed : R.color.colorGreen;
        int timerTextColor = ContextCompat.getColor(convertView.getContext(), timerTextColorId);
        viewHolder.timer.setTextColor(timerTextColor);
        viewHolder.timer.setText(AppUtils.formatTimer(order));
        return convertView;
    }
}

