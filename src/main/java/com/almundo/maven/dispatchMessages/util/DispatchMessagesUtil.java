package com.almundo.maven.dispatchMessages.util;

import java.util.Random;

public class DispatchMessagesUtil {
	
	/**
	 * Return a random time from limitFrom to limitTo in milliseconds
	 * @param limitFrom
	 * @param limitTo
	 * @return
	 */
	public static int getSpleepingTime(int limitFrom, int limitTo){
		if(limitTo > limitFrom){
			Random random = new Random();
			int seconds = limitFrom + random.nextInt(limitTo-limitFrom+1);
			return seconds*1000;
		}else{
			return 0;
		}
	}

}
