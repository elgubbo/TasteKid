package com.elgubbo.tastekid.adapter;

import java.util.List;

import com.elgubbo.tastekid.model.Result;

import android.content.Context;
import android.widget.ArrayAdapter;

public class SimpleResultAdapter extends ArrayAdapter<Result>{

	public SimpleResultAdapter(Context context,
			int textViewResourceId, List<Result> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	
}
