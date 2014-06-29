package ctdownloader.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateParser {
	
	public static Date getDateFromString(String dateInString){
		//Tue Oct 30 00:17:05 CET 2012
		dateInString = dateInString.replace("CET", "");
		SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);
		try {
			Date date = formatter.parse(dateInString);
			return date;
			
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date getDateFromString2(String dateInString){
		//2013-01-26 11:38:57 -0500
		SimpleDateFormat formatter = new SimpleDateFormat("yyy-MM-dd HH:mm:ss X");
		try {
			Date date = formatter.parse(dateInString);
			return date;
			
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
