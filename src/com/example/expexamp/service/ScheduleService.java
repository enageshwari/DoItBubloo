package com.example.expexamp.service;

import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.expexamp.service.task.AlarmTask;

public class ScheduleService extends Service {

	/**
	 * Class for clients to access
	 */
	public class ServiceBinder extends Binder {
		ScheduleService getService() {
			return ScheduleService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("ScheduleService", "Received start id " + startId + ": " + intent);
		
		// We want this service to continue running until it is explicitly stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients. See
	private final IBinder mBinder = new ServiceBinder();

	/**
	 * Show an alarm for a certain date when the alarm is called it will pop up a notification
	 */
	public void setAlarm(Calendar c, String remId , String remType) {
		// This starts a new thread to set the alarm
		// You want to push off your tasks onto a new thread to free up the UI to carry on responding
		Log.i("ScheduleService", "remId= " + remId);
		new AlarmTask(this, c , remId, remType).run();
	}
	
	public void setAlarm(Calendar c, String remId , String remType, String mailId) {
		// This starts a new thread to set the alarm
		// You want to push off your tasks onto a new thread to free up the UI to carry on responding
		Log.i("ScheduleService", "remId= " + remId);
		new AlarmTask(this, c , remId, remType,mailId).run();
	}
	
	public void cancelAlarm( String remId, String remType) {
		// This starts a new thread to set the alarm
		// You want to push off your tasks onto a new thread to free up the UI to carry on responding
		Log.i("ScheduleService", "remId= " + remId);
		new AlarmTask(this , remId, remType).runcancel();
	}
	
	
	public void cancelAlarm( String remId ) {
		// This starts a new thread to set the alarm
		// You want to push off your tasks onto a new thread to free up the UI to carry on responding
		Log.i("ScheduleService", "remId= " + remId);
		new AlarmTask(this , remId ).runcancel();
	}
	
	
}