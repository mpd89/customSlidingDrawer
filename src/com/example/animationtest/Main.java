package com.example.animationtest;

import mil.darpa.mesa.sdk.android.widgets.drawer.SlidingDrawer;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Main extends Activity
{
    boolean isSelected = false;
    boolean isSelected2 = false;
    boolean isSelected3 = false;
    boolean isSelected4 = false;
    boolean isSelected5 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SlidingDrawer mSlide = (SlidingDrawer) findViewById(R.id.slider);

        /****** Create onClickListeners ****/
        View.OnClickListener newListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("listen", "listener 1 clicked");
                if (!isSelected)
                {
                    v.setSelected(true);
                    isSelected = true;
                }
                else
                {
                    v.setSelected(false);
                    isSelected = false;
                }
            }
        };
        View.OnClickListener newListener2 = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("listen", "listener 2 clicked");
                if (!isSelected2)
                {
                    v.setSelected(true);
                    isSelected2 = true;
                }
                else
                {
                    v.setSelected(false);
                    isSelected2 = false;
                }
            }
        };
        View.OnClickListener newListener3 = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("listen", "listener 3 clicked");
                if (!isSelected3)
                {
                    v.setSelected(true);
                    isSelected3 = true;
                }
                else
                {
                    v.setSelected(false);
                    isSelected3 = false;
                }
            }
        };
        View.OnClickListener newListener4 = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("listen", "listener 4 clicked");
                if (!isSelected4)
                {
                    v.setSelected(true);
                    isSelected4 = true;
                }
                else
                {
                    v.setSelected(false);
                    isSelected4 = false;
                }
            }
        };

        View.OnClickListener newListener5 = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("listen", "listener 5 clicked");
                if (!isSelected5)
                {
                    v.setSelected(true);
                    isSelected5 = true;
                }
                else
                {
                    v.setSelected(false);
                    isSelected5 = false;
                }

            }
        };

        // Create custom stopPoints and dragDecisions
        Float[] stopPoints = { 0f, .150f, .34f, .65f, .80f, .90f, .99f };
        Float[] dragDecisions = { .50f, .5f, .50f, .50f, .50f, .50f };

        // Float[] stopPoints = {0f, .80f};
        // Float[] dragDecisions = { .50f};

        // Float[] stopPoints = {0f,.20f,.60f};
        // Float[] dragDecisions = {.50f,.50f};

        // Float[] stopPoints = {0f, .150f, .55f, .80f};
        // Float[] dragDecisions= {.5f,.5f,.5f};

        // Providing custom button images and text descriptions
        int imageArray[] = { R.drawable.my_location_buttons,
                R.drawable.node_button_list, R.drawable.details_button_list,
                R.drawable.button_draw_area_list, R.drawable.action_button_list };

        String testText[] = { "My Location", "Node", "Button", "draw area",
                "user action" };

        // SlidingDrawer set Methods
        mSlide.setDrawerList(imageArray, testText);
        mSlide.setHandleImageButton(R.drawable.tab);
        mSlide.setAnimationStopPoints(stopPoints);
        mSlide.setDragDecisionPoints(dragDecisions);
        mSlide.setHandleImagePosition(.5f, true, .76f);
        // mSlide.setFlingThreshold(300);

        // Looks up the button by its string value and adds the provided onClick listener
        mSlide.setImageButtonListener("My Location", newListener);
        mSlide.setImageButtonListener("Node", newListener2);
        mSlide.setImageButtonListener("Button", newListener3);
        mSlide.setImageButtonListener("draw area", newListener4);
        mSlide.setImageButtonListener("user action", newListener5);

        // ImageView imageView = (ImageView) findViewById(R.id.testImage);
        // imageView.setImageResource(R.drawable.surf);

        Button button = new Button(this);
        Button button2 = new Button(this);
        Button button3 = new Button(this);
        button.setText("Test");
        button2.setText("Test Test");
        button3.setText("Test Test Test");
        mSlide.setContainerView(button);
        mSlide.setContainerView(button2);
        mSlide.setContainerView(button3);
    }

    public void buttonAction(View v)
    {
    }

}
