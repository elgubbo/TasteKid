package com.elgubbo.tastekid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elgubbo.tastekid.Configuration;
import com.elgubbo.tastekid.R;
import com.elgubbo.tastekid.model.Result;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CardListArrayAdapter extends ArrayAdapter<Result>{
	
	/**
	 * A static map of possible icon resources
	 */
    public static final Map<String, Integer> iconMap;
    static {
        HashMap<String, Integer> iconMapInit = new HashMap<String, Integer>();
	    iconMapInit.put("movie", R.drawable.movie);
	    iconMapInit.put("book", R.drawable.book);
	    iconMapInit.put("music", R.drawable.music);
	    iconMapInit.put("show", R.drawable.show);
	    iconMapInit.put("game", R.drawable.game);
	    iconMapInit.put("author", R.drawable.author);
        iconMap = Collections.unmodifiableMap(iconMapInit);
    }

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
		View rowView = (convertView == null) ? inflater.inflate(
				R.layout.list_item, parent, false) : convertView;
		Result currentResult = results.get(position);
		TextView title = (TextView) rowView.findViewById(R.id.title);
		ImageView iconView = (ImageView) rowView.findViewById(R.id.iconView);
		if (Configuration.DEVMODE)
			Log.d("TasteKid", "Type of currentResult is: " + currentResult.type);
		iconView.setBackgroundResource((currentResult.type != null) ? iconMap
				.get(currentResult.type) : 0);
		title.setText(currentResult.name);
		TextView description = (TextView) rowView
				.findViewById(R.id.description);
		description.setText(currentResult.wTeaser);

		setupButtons(position, rowView);
		return rowView;
	}

	public Result getItem(int position) {
		if(results!=null)
			return results.get(position);
		else return null;
	}

	private void setupButtons(int position, View rowView) {
		LinearLayout buttonLayout = (LinearLayout) rowView
				.findViewById(R.id.buttonLayoutItem);
		addOnClickListenerToButtons(buttonLayout, getItem(position));
	}

	private void addOnClickListenerToButtons(LinearLayout buttonLayout,
			Result result) {

		if (result == null)
			return;
		LinearLayout yTButton = (LinearLayout) buttonLayout
				.findViewById(R.id.youtubeLinearLayout);
		LinearLayout wikiButton = (LinearLayout) buttonLayout
				.findViewById(R.id.wikiLinearLayout);
		ItemButtonClickListener listener = new ItemButtonClickListener(result,
				appContext);
		if (result.yID != null && !result.yID.trim().equalsIgnoreCase("")) {
			yTButton.setOnClickListener(listener);
			yTButton.setVisibility(View.VISIBLE);
		} else
			yTButton.setVisibility(View.INVISIBLE);
		if (result.wUrl != null && !result.wUrl.trim().equalsIgnoreCase("")) {
			wikiButton.setOnClickListener(listener);
			wikiButton.setVisibility(View.VISIBLE);
		} else
			wikiButton.setVisibility(View.INVISIBLE);
	}

}
