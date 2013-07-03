package com.elgubbo.tastekid;

import java.util.ArrayList;
import java.util.List;

import com.elgubbo.tastekid.model.Result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CardListArrayAdapter extends ArrayAdapter<Result>{

	Context appContext;
	ArrayList<Result> results;
	public CardListArrayAdapter(Context context,
			List<Result> objects) {
		super(context, R.layout.list_item, objects);
		this.appContext = context;
		this.results = (ArrayList<Result>) objects;
	}
	
	
	
	@Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		
	    LayoutInflater inflater = (LayoutInflater) appContext
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = (convertView == null) ? inflater.inflate(R.layout.list_item, parent, false) : convertView;
	    Result currentResult = results.get(position);
	    TextView title = (TextView) rowView.findViewById(R.id.title);
	    title.setText(currentResult.name);
	    TextView description = (TextView) rowView.findViewById(R.id.description);
	    description.setText(currentResult.wTeaser);
		return rowView;
	}


}
