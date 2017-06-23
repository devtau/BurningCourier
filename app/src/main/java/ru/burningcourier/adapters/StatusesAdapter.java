package ru.burningcourier.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import ru.burningcourier.R;
import ru.burningcourier.api.model.Status;

public class StatusesAdapter extends RecyclerView.Adapter<StatusesAdapter.ViewHolder> {

	public static final int STATUS_DELIVERED = 8;
	private List<Status> data;
	private int nextStatus;
	private boolean isCash;
	private AdapterListener listener;


	public StatusesAdapter(List<Status> data, int nextStatus, boolean isCash, AdapterListener listener) {
		this.data = data;
		this.nextStatus = nextStatus;
		this.isCash = isCash;
		this.listener = listener;
	}


	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_status, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		Status status = data.get(position);
		int statusIcon;
		if (status.getId() < nextStatus) {
			statusIcon = R.drawable.status_done;
		} else if (status.getId() == nextStatus) {
			statusIcon = R.drawable.status_active;
			holder.root.setOnClickListener(v -> listener.onStatusSelected(status));
		} else {
			statusIcon = R.drawable.status_not_active;
			holder.statusName.setTextColor(ContextCompat.getColor(holder.getContext(), R.color.colorGray174));
		}
		if (status.getId() == STATUS_DELIVERED && !isCash) {
			holder.checkPhotoRequired.setVisibility(View.VISIBLE);
		}
		holder.statusIcon.setImageResource(statusIcon);
		holder.statusName.setText(status.getTextId());
	}

	@Override
	public int getItemCount() {
		return data.size();
	}



	public interface AdapterListener {
		void onStatusSelected(Status status);
	}
	
	
	
	class ViewHolder extends RecyclerView.ViewHolder {
        
        View root;
		ImageView statusIcon;
        TextView statusName;
        TextView checkPhotoRequired;
		
		
		ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
			statusIcon = (ImageView) itemView.findViewById(R.id.statusIcon);
			statusName = (TextView) itemView.findViewById(R.id.statusName);
			checkPhotoRequired = (TextView) itemView.findViewById(R.id.checkPhotoRequired);
        }
        
        public Context getContext() {
            return root.getContext();
        }
    }
}
