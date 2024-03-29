package com.example.doitbubloo;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.expexamp.service.ScheduleClient;
 
public class NewReminder extends Activity{
	
	String remMailId = new String();
    String exisMailId = new String();
  ArrayAdapter<String> dataAdapter;
 int spinnerPosition = 0; 
 
	 final String LOGCAT = null;
	  
	 String mailId ;
		EditText editMailId;
		Calendar mailCal;
	 
	 ScheduleClient scheduleClient;
  EditText shortDesc, detailedDesc;
  
  Spinner remType;
  DBController controller = new DBController(this);
  long newTimeInMS; 
  int errflg = 0;
	int chkflag=0;
	CheckBox remDate;
	 
	String s_remDate;
	  SimpleDateFormat fmtDateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		
		Calendar cal = Calendar.getInstance();
		long curTimeInMS = cal.getTimeInMillis();
		
		
	
		
	DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
	
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, monthOfYear);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

	new TimePickerDialog(NewReminder.this, t, cal
			.get(Calendar.HOUR_OF_DAY), cal
			.get(Calendar.MINUTE), false).show();
	

	
  //updateLabel();
	}
	};

	TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);
		
		updateLabel();
	}
	};

	private void updateLabel() {
		
		newTimeInMS  = cal.getTimeInMillis();
		
		cal.set(Calendar.SECOND, 0);
		s_remDate = fmtDateAndTime.format(cal.getTime());
		
		  if ( newTimeInMS < curTimeInMS)
		  {
			  
					showAlert("Oops !!! You have selected an Invalid Time",2);
					//errflg =1;
		  }
		  
		  
	}
  @Override
  public void onCreate(Bundle savedInstanceState) {
    
	  super.onCreate(savedInstanceState);
    
    setContentView(R.layout.activity_new_reminder);
    shortDesc = (EditText) findViewById(R.id.shortDesc);
     
    detailedDesc = (EditText) findViewById(R.id.detailedDesc);
   
    remType = (Spinner) findViewById(R.id.remType);
    
    remDate = (CheckBox) findViewById(R.id.remDate);
    // IMP- if the below value is set to false then u can't request focus for this chkbox when it is invalid
    remDate.setFocusableInTouchMode(false);
remDate.setFocusable(true);
    // Create a new service client and bind our activity to this service
    scheduleClient = new ScheduleClient(this);
    scheduleClient.doBindService();
    
    
  }
  

  public void addNewReminder(View view) {
	  
	  String s_remType = remType.getSelectedItem().toString();
	  String s_shortDesc =  shortDesc.getText().toString();
	  
	  if(s_shortDesc.length() == 0)
		  	showAlert("Please enter the short description about your reminder",1);
	  else
	  {
	  
		 
		  if (! remDate.isChecked() ) 
		  {
			  Log.i("new rem","not chked");
			  calcDateForNoAlarm(s_remType);
		  }
		  
		 
		  
		 // if ( errflg == 0)
		  {
	 		 
				  Log.i("new rem","add nw rem");
			    HashMap<String, String> queryValues =  new  HashMap<String, String>();
			    queryValues.put("shortDesc",s_shortDesc);
			    queryValues.put("detailedDesc", detailedDesc.getText().toString());
			    queryValues.put("remDate", s_remDate);
			    
			    queryValues.put("remType", s_remType);
			    
			    controller.insertReminder(queryValues);
			    
			  
			    
			    String remId = controller.getLastInsertId();
			   
			    scheduleClient.setAlarmAndNotification(cal,remId,s_remType);
		 
			   this.callHomeActivity(view);
		  }
	  }
  }
  public void callHomeActivity(View v) {
    Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
    startActivity(objIntent);
  } 
  
  @Override
  protected void onStop() {
  	// When our activity is stopped ensure we also stop the connection to the service
  	// this stops us leaking our activity into the system *bad*
  	if(scheduleClient != null)
  		scheduleClient.doUnbindService();
  	super.onStop();
  }
  
  public void showAlert(String msg,final int cnt)
  {
  	   AlertDialog.Builder alert = new AlertDialog.Builder(this);
  	   	   
    	   alert.setTitle("Alert!! ");
    	   alert.setMessage(msg);
    	   alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	 	   public void onClick(DialogInterface dialog, int whichButton) {
    	 		 
    	 		   
    	 		   if(cnt ==2)
    	 		   {
    	 			remDate.setChecked(false);
        	 		remDate.requestFocus();
    	 		   }
    	 		   else if(cnt ==1)
    	 		   {
    	 			  shortDesc.requestFocus();
    	 		   }
    	 		   else if(cnt ==3)
    	 		   {
    	 			editMailId.requestFocus();
    	 			getMailIdAndSendNotification();
        	 		 
    	 		   }
    	 	  //  chkflag =0;
    	 	    //errflg =1;
    	 	   }
    	 	   });
    	 	
    	 	 
    	 	   
          	 alert.show();	

   }
  
  public void getAlarmTime(View v) {
		 
	   //is remDate checked?
		if (((CheckBox)v).isChecked()) {
			
			
			cal = Calendar.getInstance();
			new DatePickerDialog(NewReminder.this, d, cal
					.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH)).show();
		//	chkflag =1;

			
		}

	

 }
  
	public void calcDateForNoAlarm(String s_remType) {
		
		int notifyTimeChngFlg=0;
		
	      cal = Calendar.getInstance();
		
		  int hr = cal.get(Calendar.HOUR_OF_DAY);
		  int min = cal.get(Calendar.MINUTE);
		
		  if ( hr > 9 || ((hr == 9 )&&( min > 0)) )
		  {
			  notifyTimeChngFlg = 1;
			  //save current date
			  saveDateUpdateTime();
			  
		  }
		
		if(notifyTimeChngFlg == 1)
		{
		     if (s_remType.equals("No Repeat") || s_remType.equals("Daily"))
		     {
	
		    	 cal.add(Calendar.DATE,1);
		  		    	 
		     }
		     else if (s_remType.equals("Weekly") )
		     {
		    	 cal.add(Calendar.DATE,7);
		     }
		     else if (s_remType.equals("Monthly") )
		     {
		    	 cal.add(Calendar.MONTH,1);
		     }
		     else if (s_remType.equals("Yearly") )
		     {
		    	 cal.add(Calendar.YEAR,1);
		     }
		     
		     //Toast.makeText(this, " Notify Date chng to : " + fmtDateAndTime.format(cal.getTime()), //Toast.LENGTH_SHORT).show(); 
		 	
		}
		else
			saveDateUpdateTime();
	  } 
	
	public void saveDateUpdateTime()
	{
		cal.set(Calendar.HOUR_OF_DAY, 9);
		cal.set(Calendar.MINUTE, 0);

		cal.set(Calendar.SECOND, 0);//cal.set(Calendar.AM_PM,Calendar.AM);
		 
		
		s_remDate = fmtDateAndTime.format(cal.getTime());
		//Toast.makeText(this, " save Date and update Time : " + s_remDate , //Toast.LENGTH_SHORT).show(); 
	
	}
	
	 
	
	 public void getMailIdAndSendNotification(View view)
	    {
	 	   
	 	  
	 	   AlertDialog.Builder alert = new AlertDialog.Builder(this);
	 	  	   
	 	   alert.setTitle("To get Reminder Mail @ 9 AM everyday");
	 	   
	 	    LayoutInflater inflater = this.getLayoutInflater();

	 	    View v = inflater.inflate(R.layout.custom_dialog_mail,null);
	 	   
	 	    alert.setView(v);
	 	
	 	    editMailId = (EditText) v.findViewById(R.id.editMailId);
	 
	 	   alert.setPositiveButton("Start", new DialogInterface.OnClickListener() {
	 	   public void onClick(DialogInterface dialog, int whichButton) {
	 	     
	 		   
	 		  mailCal= setMailTime();
	 		   mailId = (editMailId.getText()).toString();
	 		   
	 		
	 		   String s_remType = "Mail";
	 	     scheduleClient.setAlarmAndNotification(mailCal,mailId,s_remType);
	 	   
	 	    	//sendMailNotification(mailId);  
	 	  
	 	  
	 	   }
	 	   });
	 	
	 	  alert.setNeutralButton("Stop", new DialogInterface.OnClickListener() {
		 	     public void onClick(DialogInterface dialog, int whichButton) {
		 	       // Canceled.
		 	    	 
		 	    	scheduleClient.cancelAlarmAndNotification("Mail");
		 	     }
		 	   });
		 	   
		 	   
		 	   alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			 	     public void onClick(DialogInterface dialog, int whichButton) {
			 	       // Canceled.
			 	    	 
			 	 
			 	     }
			 	   });
		 	   
	 	   
	 	   alert.show();
	 	   
	 	
		 	    
	 	  
	 	 
	    }
	    
	   
	 
	   public void sendMailNotification(String toMailId)
	   {
		
		   
		   Mail m = new Mail("suganya.rahulkanna@gmail.com", "nieo5822"); 
		   
		   String mailBody = m.getTodayRemInMailBody(this.getApplicationContext());
		   
		     if (! ( mailBody.length() == 0) )
		     {
		         String[] toArr = {toMailId}; 
		         m.setTo(toArr); 
		         m.setFrom("suganya.rahulkanna@gmail.com"); 
		         m.setSubject("Reminders for today."); 
		         m.setBody(mailBody); 
		         
		        
		         
		       
		         
		    
		         try { 
		          
		    
		           if(m.send()) { 
		        	   Log.i("MailApp", "Email was sent successfully."); 
		             //Toast.makeText(this, "Email was sent successfully.", //Toast.LENGTH_LONG).show(); 
		           } else { 
		        	   Log.i("MailApp", "Email was not sent. Reconnect to network"); 
		             //Toast.makeText(this, "Email was not sent. Reconnect to network", //Toast.LENGTH_LONG).show(); 
		           } 
		         } catch(Exception e) { 
		           
		           Log.e("MailApp", "Exception: Could not send email", e); 
		             //Toast.makeText(this, "Exception: Could not send email", //Toast.LENGTH_LONG).show(); 

		         } 
		    	
		     }
		     else 
		     {
	      	   Log.i("MailApp", "No reminder for today"); 
	       	 //Toast.makeText(this, "No reminder for today", //Toast.LENGTH_LONG).show(); 

		     } 
		   

	   }
	   
	   public Calendar setMailTime()
	   {

			Calendar cal = Calendar.getInstance();
			

		 
				
				//24 hour format
				  int hr = cal.get(Calendar.HOUR_OF_DAY);
				  int min = cal.get(Calendar.MINUTE);
				
				  if ( hr > 9 || ((hr == 9 )&&( min > 0)) )
				  {
					  cal.add(Calendar.DATE, 1);
				  }
		  
				cal.set(Calendar.HOUR_OF_DAY, 9);
				cal.set(Calendar.MINUTE, 0);

				cal.set(Calendar.SECOND, 0);//cal.set(Calendar.AM_PM,Calendar.AM);
				
				s_remDate = fmtDateAndTime.format(cal.getTime());
				//Toast.makeText(this, "You'll receive an email from : " + s_remDate , //Toast.LENGTH_SHORT).show(); 
		
				return cal;
	   }
	   
	   public void getMailIdAndSendNotification()
	    {
	 	   int iList=0;

	 		
	 		final String s_remType = "Mail";
	 	
	 		 final Spinner spinExisMailId ;

	 	    	
	 		 	 int listSize ;
	 		 	final List<String> remIdList = new ArrayList<String>();
	 			 final List<String> remMailIdList = new ArrayList<String>();
	 	   AlertDialog.Builder alert = new AlertDialog.Builder(this);
	 	   
	 	   alert.setTitle("To get Reminder Mail @ 9 AM everyday");
	 	   
	 	    LayoutInflater inflater = this.getLayoutInflater();

	 	    View v = inflater.inflate(R.layout.custom_dialog_mail,null);
	 	   int setStopOff=0;
	 	    alert.setView(v);
	 	
	 	    editMailId = (EditText) v.findViewById(R.id.editMailId);
	 	    
	 	    spinExisMailId = (Spinner) v.findViewById(R.id.exisMailIds);
	 	    
	 	 	  ArrayList<HashMap<String, String>> dbMailList = controller.getMailIdList();
	 	 	  
	 	  
	 	 	 listSize = dbMailList.size();
	 		Log.i("dbMaillist size",Integer.toString(listSize));
	 		
	 		   if( listSize != 0 )
	 		   {
	 			   while(iList  < listSize )
	 			   {
	 				   remIdList.add( dbMailList.get(iList).get("remId"));
	 				   
	 				   remMailIdList.add(dbMailList.get(iList).get("shortDesc"));
	 				   Log.i("exis mail id",dbMailList.get(iList).get("shortDesc")); 
	 				   iList++;
	 			   }
	 			   
	 			     dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, remMailIdList);
	 					dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	 					spinExisMailId.setAdapter(dataAdapter);
	 					
	 			   dataAdapter.notifyDataSetChanged();
	 			   
	 			   spinExisMailId.setVisibility(View.VISIBLE); 
	 			   setStopOff=0;
	 			  // ad.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(false);
	 		   }
	 		   else
	 		   {
	 			   setStopOff=1;
	 			   spinExisMailId.setVisibility(View.GONE);
	 		   }

	 	   alert.setPositiveButton("Start", new DialogInterface.OnClickListener() {
	 	   public void onClick(DialogInterface dialog, int whichButton) {
	 	     
	 		   
	 		   mailId = (editMailId.getText()).toString();
	 		    
	  		   
	 	 		 
	  		   if( mailId.length()!=0)
	  		   {
	 	 		  HashMap<String , String> chkMailList = controller.checkForMailIdInDB(mailId);
	 	 		  
	 	 		    if(chkMailList.size()!=0) 
	 	 		    {
	 	 		    	Log.i("chkMaillist mailid", chkMailList.get("shortDesc"));
	 	 		     showAlert("Mail ID already exists in Notification List",3);
	 	 		    //getMailIdAndSendNotification();
	 	 		    }
	 	 		    else
	 		 		{
	 	 		    	
	 	 		    	int validchk = sendSampleMail(mailId);
	 	 		    	if(validchk == 1)
	 	 		    	{
	 		 		    	 updateLabel();
	 			 		  HashMap<String, String> queryValues =  new  HashMap<String, String>();
	 					    queryValues.put("shortDesc",mailId);
	 					    queryValues.put("detailedDesc", "");
	 					    queryValues.put("remDate", "");
	 					    
	 					    queryValues.put("remType", "Mail");
	 					    
	 					    controller.insertReminder(queryValues);
	 					    
	 					    String remId = controller.getLastInsertId();
	 				 		 
	 			 	     scheduleClient.setAlarmAndNotification(cal,remId,"Mail",mailId);
	 			 	   
	 		 	     
	 	 		    	}
	 	 		    	else
	 	 		    		showAlert("Enter Valid mail ID pls",3);
	 		 	    	 
	 	 		    }
	  		   }
	  		   else
	 		    	showAlert("Enter New Id in text box",3);
	 	  
	 	   }
	 	   });
	 	
	 	  alert.setNeutralButton("Stop", new DialogInterface.OnClickListener() {
	 	 	     public void onClick(DialogInterface dialog, int whichButton) {
	 	 	       // Canceled.
	 	 	    	 
	 	 	    	  if(spinExisMailId.getVisibility() == View.VISIBLE)
	 				   {
	 					   	exisMailId  = spinExisMailId.getSelectedItem().toString();
	 					    spinnerPosition = dataAdapter.getPosition(exisMailId);
	 			    		 
	 			
	 				   }
	 				     
	 				    		remMailId =  remIdList.get(spinnerPosition) ;
	 				    		 controller.deleteReminderInfo( remMailId);
	 				 		  
	 				 		  scheduleClient.cancelAlarmAndNotification( remMailId,"Mail");
	 				 		  remMailIdList.remove(exisMailId);
	 				 		  dataAdapter.notifyDataSetChanged();
	 				    
	 				     
	 	 	     }
	 	 	   });
	 	 	   
	 	 	   
	 	 	   alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	 		 	     public void onClick(DialogInterface dialog, int whichButton) {
	 		 	       // Canceled.
	 		 	    	 
	 		 	 
	 		 	     }
	 		 	   });
	 		 	   
	 	 	  AlertDialog ad = alert.create();
	 	   ad.show();
	 	   if(setStopOff == 1)
	 		   ad.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(false);
	 	   else
	 		   ad.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(true);
	 	 
	    }
	    
	    public int sendSampleMail(String mailId)
	    {
	 	   int rv;
	   Mail m = new Mail("suganya.rahulkanna@gmail.com", "nieo5822"); 
	 	   
	 	   String mailBody = "Test Mail";
	 	   
	 	       String[] toArr = {mailId}; 
	 	         m.setTo(toArr); 
	 	         m.setFrom("suganya.rahulkanna@gmail.com"); 
	 	         m.setSubject("Do It Bubloo!!! Test Mail ..."); 
	 	         m.setBody(mailBody); 
	 	         
	 	        
	 	         
	 	       
	 	         
	 	    
	 	         try { 
	 	          
	 	    
	 	           if(m.send()) { 
	 	        	   rv=1;
	 	           } else { 
	 	        	  rv=0;
	 	           } 
	 	         } catch(Exception e) { 
	 	           
	 	          rv=0;
	 	         } 
	 	    	return rv;
	 	     
	 	      
	 	   
	    }
	    
	   
	 
}
