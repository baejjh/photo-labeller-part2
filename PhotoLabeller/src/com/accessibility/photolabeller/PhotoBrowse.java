package com.accessibility.photolabeller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

public class PhotoBrowse extends Activity implements OnClickListener {

private static final int SWIPE_MIN_DISTANCE = 120;
private static final int SWIPE_MAX_OFF_PATH = 250;
private static final int SWIPE_THRESHOLD_VELOCITY = 200;
private static final String TAG = "BROWSING";
private GestureDetector gestureDetector;
View.OnTouchListener gestureListener;
ViewFlipper imageFrame;
RelativeLayout slideShowBtn;
Handler handler;
Runnable runnable;


@Override
protected void onCreate(Bundle savedInstanceState) {
// TODO Auto-generated method stub
super.onCreate(savedInstanceState);
setContentView(R.layout.photobrowse);
imageFrame = (ViewFlipper) findViewById(R.id.imageFrames);

//get sd card path for images

File parentFolder = getFilesDir();

Log.d(TAG, parentFolder.getPath().toString());


addFlipperImages(imageFrame, parentFolder);

// Gesture detection
gestureDetector = new GestureDetector(new MyGestureDetector());
gestureListener = new View.OnTouchListener() {
public boolean onTouch(View v, MotionEvent event) {
if (gestureDetector.onTouchEvent(event))
return true;
else
return false;
}
};
handler = new Handler();
imageFrame.setOnClickListener(PhotoBrowse.this);
imageFrame.setOnTouchListener(gestureListener);
slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
slideShowBtn.setOnClickListener(new OnClickListener() {

public void onClick(View arg0) {

runnable = new Runnable() {

public void run() {
handler.postDelayed(runnable, 3000);
imageFrame.showNext();

}
};
handler.postDelayed(runnable, 500);
}
});

}


private void addFlipperImages(ViewFlipper flipper, File parent) {
int imageCount = parent.listFiles().length;
Log.d(TAG, String.valueOf(imageCount));

RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
RelativeLayout.LayoutParams.FILL_PARENT,
RelativeLayout.LayoutParams.FILL_PARENT);
for (int count = 0; count < imageCount; count++) {
ImageView imageView = new ImageView(this);
/*Bitmap imbm = BitmapFactory.decodeFile(parent.listFiles()[count]
.getAbsolutePath());
imageView.setImageBitmap(imbm);*/
String imagePath = parent.listFiles()[count].getAbsolutePath();
if (imagePath.endsWith(".jpg"))
{
	try {
		FileInputStream imageStream = new FileInputStream(imagePath);
		Bitmap imbm = BitmapFactory.decodeFileDescriptor(imageStream.getFD());
		imageView.setImageBitmap(imbm);
		imageView.setLayoutParams(params);
		flipper.addView(imageView);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	//imageView.setImageDrawable(Drawable.createFromPath(imagePath));
	//System.gc();	
}


}

}
class MyGestureDetector extends SimpleOnGestureListener {
@SuppressWarnings("static-access")
public boolean onSingleTapConfirmed(MotionEvent e) {
// TODO Auto-generated method stub
slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
slideShowBtn.setVisibility(slideShowBtn.VISIBLE);
handler.removeCallbacks(runnable);
runnable = new Runnable() {

public void run() {
slideShowBtn.setVisibility(slideShowBtn.INVISIBLE);
}
};
handler.postDelayed(runnable, 2000);
return true;
}

@SuppressWarnings("static-access")
public boolean onSingleTapUp(MotionEvent e) {
// TODO Auto-generated method stub
slideShowBtn = (RelativeLayout) findViewById(R.id.slideShowBtn);
slideShowBtn.setVisibility(slideShowBtn.VISIBLE);
handler.removeCallbacks(runnable);
runnable = new Runnable() {

public void run() {
slideShowBtn.setVisibility(slideShowBtn.INVISIBLE);
}
};
handler.postDelayed(runnable, 2000);
return true;
}

public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
float velocityY) {
try {
if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
return false;
// right to left swipe
if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
handler.removeCallbacks(runnable);
imageFrame.setInAnimation(inFromRightAnimation());
imageFrame.setOutAnimation(outToLeftAnimation());
imageFrame.showNext();
} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
handler.removeCallbacks(runnable);
imageFrame.setInAnimation(inFromLeftAnimation());
imageFrame.setOutAnimation(outToRightAnimation());
imageFrame.showPrevious();
}
} catch (Exception e) {
// nothing
}
return false;
}

}

public void onClick(View view) {

}

private Animation inFromRightAnimation() {

Animation inFromRight = new TranslateAnimation(
Animation.RELATIVE_TO_PARENT, +1.2f,
Animation.RELATIVE_TO_PARENT, 0.0f,
Animation.RELATIVE_TO_PARENT, 0.0f,
Animation.RELATIVE_TO_PARENT, 0.0f);
inFromRight.setDuration(500);
inFromRight.setInterpolator(new AccelerateInterpolator());
return inFromRight;
}
private Animation outToLeftAnimation() {
Animation outtoLeft = new TranslateAnimation(
Animation.RELATIVE_TO_PARENT, 0.0f,
Animation.RELATIVE_TO_PARENT, -1.2f,
Animation.RELATIVE_TO_PARENT, 0.0f,
Animation.RELATIVE_TO_PARENT, 0.0f);
outtoLeft.setDuration(500);
outtoLeft.setInterpolator(new AccelerateInterpolator());
return outtoLeft;
}
private Animation inFromLeftAnimation() {
Animation inFromLeft = new TranslateAnimation(
Animation.RELATIVE_TO_PARENT, -1.2f,
Animation.RELATIVE_TO_PARENT, 0.0f,
Animation.RELATIVE_TO_PARENT, 0.0f,
Animation.RELATIVE_TO_PARENT, 0.0f);
inFromLeft.setDuration(500);
inFromLeft.setInterpolator(new AccelerateInterpolator());
return inFromLeft;
}
private Animation outToRightAnimation() {
Animation outtoRight = new TranslateAnimation(
Animation.RELATIVE_TO_PARENT, 0.0f,
Animation.RELATIVE_TO_PARENT, +1.2f,
Animation.RELATIVE_TO_PARENT, 0.0f,
Animation.RELATIVE_TO_PARENT, 0.0f);
outtoRight.setDuration(500);
outtoRight.setInterpolator(new AccelerateInterpolator());
return outtoRight;
}

}

