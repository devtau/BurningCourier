package ru.burningcourier.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ru.burningcourier.R;
import ru.burningcourier.ui.AuthInteractionListener;
import ru.burningcourier.utils.AppUtils;

public class CityListFragment extends Fragment {
    
    private AuthInteractionListener listener;
    private ArrayAdapter<CharSequence> adapter;
    private MenuItem cityMenuItem;
    
    
    public CityListFragment() { }
    
    public static CityListFragment newInstance() {
        return new CityListFragment();
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter = ArrayAdapter.createFromResource(context, R.array.cities, android.R.layout.simple_list_item_1);
        try {
            listener = (AuthInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement AuthInteractionListener");
        }
        ((AppCompatActivity) context).setTitle(getString(R.string.city_title));
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city, container, false);
        ListView cityList = (ListView) view.findViewById(R.id.cityList);
        cityList.setAdapter(adapter);
        cityList.setOnItemClickListener((parent, v, position, id) -> {
            AppUtils.setAPIBase(getContext(), position);
            listener.saveCity(position);
            cityMenuItem.setVisible(true);
        });
        return view;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        cityMenuItem = menu.findItem(R.id.action_city).setVisible(false);
    }
}
