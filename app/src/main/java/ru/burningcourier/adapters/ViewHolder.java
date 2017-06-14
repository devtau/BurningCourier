package ru.burningcourier.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import ru.burningcourier.R;

class ViewHolder {
    
    View root;
    ImageView itemImg;
    TextView orderNum;
    TextView address;
    TextView phone;
    TextView note;
    TextView timer;
    
    ViewHolder(View itemView) {
        root = itemView;
        itemImg = (ImageView) itemView.findViewById(R.id.itemImg);
        orderNum = (TextView) itemView.findViewById(R.id.orderNum);
        address = (TextView) itemView.findViewById(R.id.address);
        phone = (TextView) itemView.findViewById(R.id.phone);
        note = (TextView) itemView.findViewById(R.id.note);
        timer = (TextView) itemView.findViewById(R.id.timer);
    }
}
