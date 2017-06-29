package com.cinetpay.sdk.tool;

import java.util.Date;

import android.os.AsyncTask;
/*
 * Copyright (C) 2014 Istat Dev.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 * @author Toukea Tatsi (Istat)
 *
 */
public abstract class AdvancedAsyncTask<params, progress, results> extends AsyncTask<params, progress, results>{
	
		private boolean run=true,complete=false;
		private long startTimeStamp=0;
		private long endTimeStamp=0;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			run=true;
			startTimeStamp=new Date().getTime();
		}
		
		protected void onCancelled() {
			
			run=false;
			complete=false;
			endTimeStamp=new Date().getTime();
		}
		protected void onPostExecute(results result) {
			run=false;
			complete=true;
			endTimeStamp=new Date().getTime();
			
		};
		public boolean isComplete() {
			if(isCancelled()) return false;
			return complete;
		}
		public boolean isRun() {
			return run;
		}
		public long getExecutionTime(){
			if(endTimeStamp<=startTimeStamp)return getDuration();
			return endTimeStamp-startTimeStamp;
		}
		public long getDuration(){
			return new Date().getTime()-startTimeStamp;
		}
		
	
}
