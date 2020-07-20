package my.android.paintbook;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

public class ActionsRing extends Activity {
	private ImageView pallete;
	private int mColor = Color.BLACK;

	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 LayoutInflater factory = LayoutInflater.from(this);
		 final View xmlRing = factory.inflate(R.layout.actions, null);
		 setContentView(xmlRing);
		 pallete = (ImageView)findViewById(R.id.pallete);
		 pallete.setOnTouchListener(imgSourceOnTouchListener);
	 }
	  
	 OnTouchListener imgSourceOnTouchListener = new OnTouchListener(){
	  @Override
	  public boolean onTouch(View view, MotionEvent event) {  
		  float eventX = event.getX();
		  float eventY = event.getY();
		  int x = Integer.valueOf((int)eventX);
		  int y = Integer.valueOf((int)eventY);
		  Drawable imgDrawable = ((ImageView)view).getDrawable();
		  Bitmap bitmap = ((BitmapDrawable)imgDrawable).getBitmap();  
		  mColor = bitmap.getPixel(x, y);	
		  PaintBook.paint.setColor(mColor);
		  return true;
	  }};
	 
	 public void hideMenu(View v) {
		 this.finish();
	 }
	 
	 public void FillColor(View v) {
		 PaintBook.bitmap.eraseColor(Color.WHITE);
		 this.finish();
	 }
	 
	 public void setPencil(View v) {
		 PaintBook.paint.setXfermode(null);
		 PaintBook.paint.setStrokeWidth(1);
		 PaintBook.paint.setMaskFilter(null);
		 this.finish();
	 }
	 
	 public void setPen(View v) {
		 PaintBook.paint.setXfermode(null);
		 PaintBook.paint.setStrokeWidth(2);
		 PaintBook.paint.setMaskFilter(null);
		 this.finish();
	 }

	 
	 public void setMarker(View v) {
		 PaintBook.paint.setXfermode(null);
		 MaskFilter emboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },  0.4f, 6, 3.5f);
		 PaintBook.paint.setStrokeWidth(4);
		 PaintBook.paint.setMaskFilter(emboss);
		 this.finish();
	 }
	 
	 public void setBrush(View v) {
		 PaintBook.paint.setXfermode(null);
		 MaskFilter blur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);   //INNER, OUTER
		 PaintBook.paint.setStrokeWidth(10);
		 PaintBook.paint.setMaskFilter(blur);
		 this.finish();
	 }
	 
	 public void setRoll(View v) {
		 PaintBook.paint.setXfermode(null);
		 MaskFilter blur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);   //INNER, OUTER
		 PaintBook.paint.setStrokeWidth(40);
		 PaintBook.paint.setMaskFilter(blur);
		 this.finish();
	 }
	 
	 public void setEraser(View v) {
		 PaintBook.paint.setStrokeWidth(20);
		 PaintBook.paint.setMaskFilter(null);
		 PaintBook.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		 this.finish();
	 }
	
}
