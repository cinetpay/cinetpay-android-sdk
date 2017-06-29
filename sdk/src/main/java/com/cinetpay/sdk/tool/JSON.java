package com.cinetpay.sdk.tool;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSON {

	public static List<JSONObject> JSONArrayToJsonList(JSONArray array) throws JSONException{
		List<JSONObject> out=new ArrayList<JSONObject>();
		for(int i=0 ;i<array.length();i++)
			out.add(array.getJSONObject(i));
		return out;
	}
	public static List<String> JSONArrayToStringList(JSONArray array) throws JSONException{
		List<String> out=new ArrayList<String>();
		for(int i=0 ;i<array.length();i++)
			out.add(array.getString(i));
		return out;
	}
}
