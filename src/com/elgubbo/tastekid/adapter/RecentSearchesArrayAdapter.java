package com.elgubbo.tastekid.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.elgubbo.tastekid.R;
import com.elgubbo.tastekid.TasteKidActivity;
import com.elgubbo.tastekid.model.ApiResponse;
import com.elgubbo.tastekid.model.Result;

public class RecentSearchesArrayAdapter extends ArrayAdapter<ApiResponse>{
	
	List<ApiResponse> results;
	
	public RecentSearchesArrayAdapter(Context context, int resource,
			List<ApiResponse> list) {
		super(context, resource, list);
		this.results = list;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) TasteKidActivity.getAppContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(convertView == null)
			convertView = inflater.inflate(
					R.layout.sidebar_list_item, parent, false);
		ApiResponse currentResult = results.get(position);
		TextView title = (TextView) convertView.findViewById(R.id.itemTitle);
		title.setText(currentResult.query);
/*		TextView description = (TextView) convertView
				.findViewById(R.id.description);
		description.setText(currentResult.wTeaser);*/

//		setupButtons(position, convertView);
		

		return convertView;
	}

	
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getItem(int)
	 */
	@Override
	public ApiResponse getItem(int position) {
		if(results!=null && results.size()!=0)
			return results.get(position);
		else return null;
	}
}
