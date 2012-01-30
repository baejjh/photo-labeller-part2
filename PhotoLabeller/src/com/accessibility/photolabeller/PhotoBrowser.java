package com.accessibility.photolabeller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.accessibility.photolabeller.PhotoBrowser;
import com.accessibility.photolabeller.R;
import com.accessibility.photolabeller.PhotoBrowser.FileUtils;
import com.accessibility.photolabeller.PhotoBrowser.MyGestureDetector;

public class PhotoBrowser extends Activity implements OnPreparedListener{
    
    private static final int EXIT = 0;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private static final String DIRECTORY = "/sdcard/";
    private static final String DATA_DIRECTORY = "/sdcard/.ImageViewFlipper/";
    private static final String DATA_FILE = "/sdcard/.ImageViewFlipper/imagelist.dat";
	private static final String TAG = "PHOTO_BROWSER";
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    private Animation slideLeftIn;
    private Animation slideLeftOut;
    private Animation slideRightIn;
    private Animation slideRightOut;
    private ViewFlipper viewFlipper;
    private int currentView = 0;
    List<String> ImageList;
    List<String> AudioList;
    private int currentIndex = 0;
    private int maxIndex = 0;
    
    MediaPlayer mp = new MediaPlayer();

    FileOutputStream output = null;
    OutputStreamWriter writer = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.photobrowser);
        ImageView iv = (ImageView) findViewById(R.id.zero);
        //ImageView iv = new ImageView(this);
        mp.setOnPreparedListener(this);
        if (iv == null) {
        	Log.d("TAG", "iv isss null!");
        }
        

        //File data_directory = new File(DATA_DIRECTORY);
        File data_directory = getFilesDir();
        Log.d("TAG", data_directory.toString());
        if (!data_directory.exists()) {
        	Log.d("TAG", "data directory does not exists!");
            if (data_directory.mkdir()) {
            	Log.d("TAG", "make directory successful!");
                FileUtils savedata = new FileUtils();
                Toast toast = Toast.makeText(PhotoBrowser.this,
                        "Please wait while we search your SD Card for images...", Toast.LENGTH_SHORT);
                toast.show();
                SystemClock.sleep(100);
                ImageList = FindFiles();
                savedata.saveArray(DATA_FILE, ImageList);
                
            } else {
            	Log.d("TAG", "make directory is not successful!");
                ImageList = FindFiles();
                Log.d("TAG", String.valueOf(ImageList.size()));
            }

        }
        else {
            //File data_file= new File(DATA_FILE);
        	ImageList = new ArrayList<String>();
        	AudioList = new ArrayList<String>();
        	File data_file = getFilesDir();
        	int imageCount = data_file.listFiles().length;
        	Log.d("TAG", data_file.listFiles()[0].getAbsolutePath());
        	Log.d("TAG", data_file.toString());
        	Log.d("TAG", String.valueOf(imageCount));
            if (!data_file.exists()) {
                FileUtils savedata = new FileUtils();
                Toast toast = Toast.makeText(PhotoBrowser.this,
                        "Please wait while we search your SD Card for images...", Toast.LENGTH_SHORT);
                toast.show();
                SystemClock.sleep(100);
                ImageList = FindFiles();
                savedata.saveArray(DATA_FILE, ImageList);
            } else {
            	Log.d("TAG", "inside else!");
                FileUtils readdata = new FileUtils();
                
                Log.d("TAG", "just before the for loop!");
                for(int i = 0; i < imageCount; i++) {
                	Log.d("TAG", String.valueOf(i));
                	
                	String s = data_file.listFiles()[i].getAbsolutePath();
                	if (s.endsWith(".jpg")) {
                		ImageList.add(s);
                	}
                	else {
                		AudioList.add(s);
                	}
                }
                //ImageList = readdata.loadArray(data_file.toString());
                if (ImageList == null) {
                	Log.d("TAG", "image list is null!");
                }
                Log.d("TAG", String.valueOf(ImageList.size()));
            }
        }
        
        if (ImageList == null) {
        	Log.d("TAG", "ImageList is null");
            quit();
        }
        
        SharedPreferences indexPrefs = getSharedPreferences("currentIndex",
                MODE_PRIVATE);
        if (indexPrefs.contains("currentIndex")) {
            currentIndex = indexPrefs.getInt("currentIndex", 0);
        }
        
        maxIndex = ImageList.size() - 1;
        
        Log.v("currentIndex", "Index: "+currentIndex);

        viewFlipper = (ViewFlipper) findViewById(R.id.flipper);
        
        Log.d("TAG", "1");

        slideLeftIn = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
        
        Log.d("TAG", "2");
        
        slideLeftOut = AnimationUtils
                .loadAnimation(this, R.anim.slide_left_out);
        
        Log.d("TAG", "3");
        
        slideRightIn = AnimationUtils
                .loadAnimation(this, R.anim.slide_right_in);
        
        Log.d("TAG", "4");
        
        slideRightOut = AnimationUtils.loadAnimation(this,
                R.anim.slide_right_out);
        
        Log.d("TAG", "5");

        /*viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));*/
        
        Log.d("TAG", "6");
        
        /*viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));*/
        
        Log.d("TAG", "just before iv.setImageDrawable");
        String s = ImageList.get(currentIndex);
        Log.d("TAG", "picture path is: " + s);
        if (iv == null) {
        	Log.d("TAG", "iv is null");
        }
        iv.setImageDrawable(Drawable.createFromPath(s));
        // creating audio file path
        String audioPath = s.replace(".jpg", ".3gp");
        // get file name only
        int startIndex = audioPath.indexOf("tm_file");
        String filename = audioPath.substring(startIndex);
        Log.d(TAG, "audio file name : " + filename);
        
        //Log.d(TAG, "Audio Path: " + audioPath);
        if (true/*AudioList.contains(audioPath)*/){
        	Log.d(TAG, Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/" +filename);
        	String extPath =  Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/" +filename;
        	playTag(extPath);
        }
        
        
        
        Log.d("TAG", "just after iv.setImageDrawable");
        System.gc();
        
        gestureDetector = new GestureDetector(new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };

    }
    
    private void playTag(String audioPath){
		    
    		try {
				mp.setDataSource(audioPath);
			} catch (IllegalArgumentException e) {
				Log.e(TAG, e.getMessage().toString());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage().toString());
				e.printStackTrace();
			}
    	
		try {
			//mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
			//mp.setDataSource(audioPath);
			
			mp.prepareAsync();
		} catch (IllegalStateException e) {
			Log.e(TAG, e.getMessage().toString());
			//e.printStackTrace();
		}
		
		
    	
		
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        int NONE = Menu.NONE;
        menu.add(NONE, EXIT, NONE, "Exit");
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case EXIT:
            quit();
            break;
        }

        return super.onOptionsItemSelected(item);
    }
    
    protected void onPause() {
        super.onPause();

        SharedPreferences indexPrefs = getSharedPreferences("currentIndex",
                MODE_PRIVATE);
        
        SharedPreferences.Editor indexEditor = indexPrefs.edit();
        indexEditor.putInt("currentIndex", currentIndex);
        indexEditor.commit();
    }
    
    protected void onResume() {
        super.onResume();
        SharedPreferences indexPrefs = getSharedPreferences("currentIndex",
                MODE_PRIVATE);
        if (indexPrefs.contains("currentIndex")) {
            currentIndex = indexPrefs.getInt("currentIndex", 0);
        }   
    }

    private List<String> FindFiles() {
        final List<String> tFileList = new ArrayList<String>();
        Resources resources = getResources();
        // array of valid image file extensions
        String[] imageTypes = resources.getStringArray(R.array.image);
        FilenameFilter[] filter = new FilenameFilter[imageTypes.length];

        int i = 0;
        for (final String type : imageTypes) {
            filter[i] = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith("." + type);
                }
            };
            i++;
        }

        FileUtils fileUtils = new FileUtils();
        File[] allMatchingFiles = fileUtils.listFilesAsArray(
                new File(DIRECTORY), filter, -1);
        for (File f : allMatchingFiles) {
            tFileList.add(f.getAbsolutePath());
        }
        return tFileList;
    }

    public class FileUtils {
        
        public void saveArray(String filename, List<String> output_field) {
             try {
                FileOutputStream fos = new FileOutputStream(filename);
                GZIPOutputStream gzos = new GZIPOutputStream(fos);
                ObjectOutputStream out = new ObjectOutputStream(gzos);
                out.writeObject(output_field);
                out.flush();
                out.close();
             }
             catch (IOException e) {
                 e.getStackTrace(); 
             }
          }

          @SuppressWarnings("unchecked")
        public List<String> loadArray(String filename) {
              try {
                FileInputStream fis = new FileInputStream(filename);
                GZIPInputStream gzis = new GZIPInputStream(fis);
                ObjectInputStream in = new ObjectInputStream(gzis);
                List<String> read_field = (List<String>)in.readObject();
                in.close();
                return read_field;
              }
              catch (Exception e) {
                  e.getStackTrace();
              }
              return null;
          }
          
        public File[] listFilesAsArray(File directory, FilenameFilter[] filter,
                int recurse) {
            Collection<File> files = listFiles(directory, filter, recurse);

            File[] arr = new File[files.size()];
            return files.toArray(arr);
        }

        public Collection<File> listFiles(File directory,
                FilenameFilter[] filter, int recurse) {

            Vector<File> files = new Vector<File>();

            File[] entries = directory.listFiles();

            if (entries != null) {
                for (File entry : entries) {
                    for (FilenameFilter filefilter : filter) {
                        if (filter == null
                                || filefilter
                                        .accept(directory, entry.getName())) {
                            files.add(entry);
                            Log.v("ImageViewFlipper", "Added: "
                                    + entry.getName());
                        }
                    }
                    if ((recurse <= -1) || (recurse > 0 && entry.isDirectory())) {
                        recurse--;
                        files.addAll(listFiles(entry, filter, recurse));
                        recurse++;
                    }
                }
            }
            return files;
        }
    }

    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    viewFlipper.setInAnimation(slideLeftIn);
                    viewFlipper.setOutAnimation(slideLeftOut);

                    if (currentIndex == maxIndex) {
                        currentIndex = 0;
                    } else {
                        currentIndex = currentIndex + 1;
                    }
                    if (currentView == 0) {
                        currentView = 1;
                        ImageView iv = (ImageView) findViewById(R.id.one);
                        iv.setImageDrawable(Drawable.createFromPath(ImageList
                                .get(currentIndex)));
                        System.gc();
                    } else if (currentView == 1) {
                        currentView = 2;
                        ImageView iv = (ImageView) findViewById(R.id.two);
                        iv.setImageDrawable(Drawable.createFromPath(ImageList
                                .get(currentIndex)));
                        System.gc();
                    } else {
                        currentView = 0;
                        ImageView iv = (ImageView) findViewById(R.id.zero);
                        iv.setImageDrawable(Drawable.createFromPath(ImageList
                                .get(currentIndex)));
                        System.gc();
                    }
                    Log.v("ImageViewFlipper", "Current View: " + currentView);
                    viewFlipper.showNext();
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    viewFlipper.setInAnimation(slideRightIn);
                    viewFlipper.setOutAnimation(slideRightOut);
                    if (currentIndex == 0) {
                        currentIndex = maxIndex;
                    } else {
                        currentIndex = currentIndex - 1;
                    }
                    if (currentView == 0) {
                        currentView = 2;
                        ImageView iv = (ImageView) findViewById(R.id.two);
                        iv.setImageDrawable(Drawable.createFromPath(ImageList
                                .get(currentIndex)));
                    } else if (currentView == 2) {
                        currentView = 1;
                        ImageView iv = (ImageView) findViewById(R.id.one);
                        iv.setImageDrawable(Drawable.createFromPath(ImageList
                                .get(currentIndex)));
                    } else {
                        currentView = 0;
                        ImageView iv = (ImageView) findViewById(R.id.zero);
                        iv.setImageDrawable(Drawable.createFromPath(ImageList
                                .get(currentIndex)));
                    }
                    Log.v("ImageViewFlipper", "Current View: " + currentView);
                    viewFlipper.showPrevious();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event))
            return true;
        else
            return false;
    }
    
    public void quit() {
        SharedPreferences indexPrefs = getSharedPreferences("currentIndex",
                MODE_PRIVATE);
        
        SharedPreferences.Editor indexEditor = indexPrefs.edit();
        indexEditor.putInt("currentIndex", 0);
        indexEditor.commit();
        
        File settings = new File(DATA_FILE);
        settings.delete();
        finish();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        System.exit(0);
    }

	@Override
	public void onPrepared(MediaPlayer arg0) {
		mp.start();
		Log.d(TAG, "ONPREPARED_LISTENER");
	}


}
