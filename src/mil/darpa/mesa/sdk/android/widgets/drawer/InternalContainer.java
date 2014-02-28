package mil.darpa.mesa.sdk.android.widgets.drawer;

import com.example.animationtest.R;
import com.example.animationtest.R.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.view.View.OnTouchListener;

public class InternalContainer extends FrameLayout implements OnTouchListener {
    private Context ctx;
    private GestureDetector mDetector;

    public InternalContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;

        setOnTouchListener(this);
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) ctx.getSystemService(infService);
        li.inflate(R.layout.internal_container, this, true);
    }

    public void buttonAction(View v) {

    }

    @Override
    public boolean onTouch(View arg0, MotionEvent arg1) {
        // TODO Auto-generated method stub
        Log.i("container", "On Touch Hit");
        return true;
    }

}
