package com.elgubbo.tastekid;

import android.support.v4.app.FragmentActivity;

import com.elgubbo.tastekid.api.TasteKidSpiceService;
import com.octo.android.robospice.SpiceManager;

public abstract class BaseTasteKidSpiceActivity extends FragmentActivity{

	
    private SpiceManager spiceManager = new SpiceManager( TasteKidSpiceService.class );

    public SpiceManager getSpiceManager() {
        return spiceManager;
    }

    @Override
    protected void onStart() {
        spiceManager.start( this );
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }
    
    
	
}
