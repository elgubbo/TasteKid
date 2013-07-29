package com.elgubbo.tastekid;

import com.elgubbo.tastekid.api.TasteKidSpiceService;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.octo.android.robospice.SpiceManager;

public abstract class BaseTasteKidSpiceActivity extends SlidingFragmentActivity{

	
    private SpiceManager spiceManager = new SpiceManager( TasteKidSpiceService.class );

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

    public SpiceManager getSpiceManager() {
        return spiceManager;
    }
	
}
