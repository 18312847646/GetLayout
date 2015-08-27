package midsummer.getlayout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity
{
	@ViewById
	Button button;
	
	@Click(R.id.button)
	public void click()
	{
		startService(new Intent(this, MyService.class));
	}
}