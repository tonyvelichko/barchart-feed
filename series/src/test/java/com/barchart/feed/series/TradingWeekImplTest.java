package com.barchart.feed.series;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.joda.time.DateTime;
import org.junit.Test;

import com.barchart.feed.api.series.Period;
import com.barchart.feed.api.series.PeriodType;
import com.barchart.feed.api.series.TradingSession;
import com.barchart.feed.api.series.TradingWeek;


public class TradingWeekImplTest {
    private final String TEST = "TEST";
    private final String TEST3 = "TEST3";
    
    @Test
    public void testGetTradingSessionOnOrBefore() {
        TradingWeek tradingWeek = getTestTradingWeek(TEST);
        
        assertEquals(7, tradingWeek.length());
        
        //FIRST: Test within bounds Sunday, May 3 - Saturday, May 9
        DateTime testDate = new DateTime(2009, 5, 9, 8, 30, 0, 0);
        TradingSession session = tradingWeek.getTradingSessionOnOrBefore(testDate);
        assertEquals("Saturday(08:30:00.000-15:30:00.000)", session.toString());
        
        testDate = new DateTime(2009, 5, 8, 8, 30, 0, 0);
        session = tradingWeek.getTradingSessionOnOrBefore(testDate);
        assertEquals("Friday(08:30:00.000-15:30:00.000)", session.toString());
        
        testDate = new DateTime(2009, 5, 7, 8, 30, 0, 0);
        session = tradingWeek.getTradingSessionOnOrBefore(testDate);
        assertEquals("Thursday(08:30:00.000-15:30:00.000)", session.toString());
        
        testDate = new DateTime(2009, 5, 6, 8, 30, 0, 0);
        session = tradingWeek.getTradingSessionOnOrBefore(testDate);
        assertEquals("Wednesday(08:30:00.000-15:30:00.000)", session.toString());
        
        testDate = new DateTime(2009, 5, 5, 8, 30, 0, 0);
        session = tradingWeek.getTradingSessionOnOrBefore(testDate);
        assertEquals("Tuesday(08:30:00.000-15:30:00.000)", session.toString());
        
        testDate = new DateTime(2009, 5, 4, 8, 30, 0, 0);
        session = tradingWeek.getTradingSessionOnOrBefore(testDate);
        assertEquals("Monday(08:30:00.000-15:30:00.000)", session.toString());
        
        testDate = new DateTime(2009, 5, 3, 8, 30, 0, 0);
        session = tradingWeek.getTradingSessionOnOrBefore(testDate);
        assertEquals("Sunday(08:30:00.000-15:30:00.000)", session.toString());
        
        //////////////////////////////////////////////////////////
        //SECOND: Test receding to previous session
        //Receding due to days
        testDate = new DateTime(2009, 5, 10, 5, 30, 0, 0); //Sunday, May 10th
        session = tradingWeek.getTradingSessionOnOrBefore(testDate);
        assertEquals("Saturday(08:30:00.000-15:30:00.000)", session.toString());
        
        //Receding due to hours
        testDate = new DateTime(2009, 5, 9, 16, 30, 0, 0); //Saturday, May 9th
        session = tradingWeek.getTradingSessionOnOrBefore(testDate);
        assertEquals("Saturday(08:30:00.000-15:30:00.000)", session.toString());
        
        //Receding due to minutes
        testDate = new DateTime(2009, 5, 9, 15, 31, 0, 0);
        session = tradingWeek.getTradingSessionOnOrBefore(testDate);
        assertEquals("Saturday(08:30:00.000-15:30:00.000)", session.toString());
        
        //Receding due to seconds
        testDate = new DateTime(2009, 5, 9, 15, 30, 1, 0);
        session = tradingWeek.getTradingSessionOnOrBefore(testDate);
        assertEquals("Saturday(08:30:00.000-15:30:00.000)", session.toString());
    }
    
    @Test
    public void testGetPreviousSessionDate() {
        //Test recession to preceding week
        DateTime testDate = new DateTime(2009, 5, 5, 0, 0, 0, 999);//Tuesday, May 5th
        TradingWeek tradingWeek = getTestTradingWeek(TEST3); //Trading Week includes only Tue, Wed, Thur
        Period tu = new Period(PeriodType.DAY, 1);
        DateTime previousDate = tradingWeek.getPreviousSessionDate(testDate, tu);
        assertEquals("2009-04-30T15:30:00.000", noZone(previousDate.toString()));
        
        testDate = new DateTime(2009, 5, 4, 0, 0, 0, 999);//Monday, May 4th
        previousDate = tradingWeek.getPreviousSessionDate(testDate, tu);
        assertEquals("2009-04-30T15:30:00.000", noZone(previousDate.toString()));
        
        testDate = new DateTime(2009, 5, 3, 0, 0, 0, 999);//Sunday, May 3rd
        previousDate = tradingWeek.getPreviousSessionDate(testDate, tu);
        assertEquals("2009-04-30T15:30:00.000", noZone(previousDate.toString()));
        
        testDate = new DateTime(2009, 5, 2, 0, 0, 0, 999);//Saturday, May 2nd
        previousDate = tradingWeek.getPreviousSessionDate(testDate, tu);
        assertEquals("2009-04-30T15:30:00.000", noZone(previousDate.toString()));
        
        testDate = new DateTime(2009, 5, 1, 0, 0, 0, 999);//Friday, May 1st
        previousDate = tradingWeek.getPreviousSessionDate(testDate, tu);
        //Due to the test date being midnight, the last session of 04-30 doesn't contain
        //the time and so the previous session to that is returned. As opposed to the
        //test below which tests for an actual valid session time.
        assertEquals("2009-04-29T15:30:00.000", noZone(previousDate.toString()));
        
        //Adjustment which correlates to the discussion above
        testDate = new DateTime(2009, 5, 1, 12, 0, 0, 999);//Friday, May 1st
        previousDate = tradingWeek.getPreviousSessionDate(testDate, tu);
        assertEquals("2009-04-30T15:30:00.000", noZone(previousDate.toString()));
        
        ///////////////////////////////////////////////////
        
        tradingWeek = getTestTradingWeek(TEST);
        
        testDate = new DateTime(2009, 5, 1, 12, 0, 0, 999);//Friday, May 1st
        
        tu = new Period(PeriodType.YEAR, 1);
        previousDate = tradingWeek.getPreviousSessionDate(testDate, tu);
        assertEquals("2008-12-31T15:30:00.000", noZone(previousDate.toString()));
        
        tu = new Period(PeriodType.QUARTER, 1);
        previousDate = tradingWeek.getPreviousSessionDate(testDate, tu);
        assertEquals("2009-03-31T15:30:00.000", noZone(previousDate.toString()));
        
        tu = new Period(PeriodType.MONTH, 1);
        previousDate = tradingWeek.getPreviousSessionDate(testDate, tu);
        assertEquals("2009-04-30T15:30:00.000", noZone(previousDate.toString()));
        
        tu = new Period(PeriodType.WEEK, 1);
        previousDate = tradingWeek.getPreviousSessionDate(testDate, tu);
        //Advances to the end of the week so not 04-24
        assertEquals("2009-04-25T15:30:00.000", noZone(previousDate.toString()));
        
        tu = new Period(PeriodType.DAY, 1);
        previousDate = tradingWeek.getPreviousSessionDate(testDate, tu);
        assertEquals("2009-04-30T15:30:00.000", noZone(previousDate.toString()));
        
        testDate = new DateTime(2009, 5, 1, 8, 30, 0, 999);
        tu = new Period(PeriodType.HOUR, 1);
        previousDate = tradingWeek.getPreviousSessionDate(testDate, tu);
        //Tests rollunder
        assertEquals("2009-04-30T15:30:00.000", noZone(previousDate.toString()));
        
        testDate = new DateTime(2009, 5, 1, 9, 30, 0, 999);
        tu = new Period(PeriodType.HOUR, 1);
        previousDate = tradingWeek.getPreviousSessionDate(testDate, tu);
        assertEquals("2009-05-01T08:30:00.000", noZone(previousDate.toString()));
        
        tu = new Period(PeriodType.MINUTE, 1);
        previousDate = tradingWeek.getPreviousSessionDate(testDate, tu);
        assertEquals("2009-05-01T09:29:00.000", noZone(previousDate.toString()));
        
        tu = new Period(PeriodType.SECOND, 1);
        previousDate = tradingWeek.getPreviousSessionDate(testDate, tu);
        assertEquals("2009-05-01T09:29:59.000", noZone(previousDate.toString()));
        
        //Test recession to preceding week
        testDate = new DateTime(2009, 5, 1, 12, 0, 0, 999);//Friday, May 1st
        tradingWeek = getTestTradingWeek(TEST3); //Trading Week includes only Tue, Wed, Thur
        tu = new Period(PeriodType.DAY, 1);
        previousDate = tradingWeek.getPreviousSessionDate(testDate, tu);
        assertEquals("2009-04-30T15:30:00.000", noZone(previousDate.toString()));
    }
    
    /**
     * Test that we can get the correct session containing a given DateTime.
     */
    @Test
    public void testGetTradingSessionOnOrAfter() {
        TradingWeek tradingWeek = getTestTradingWeek(TEST);
        
        assertEquals(7, tradingWeek.length());
        
        //FIRST: Test within bounds Sunday, May 3 - Saturday, May 9
        DateTime testDate = new DateTime(2009, 5, 3, 8, 30, 0, 0);
        TradingSession session = tradingWeek.getTradingSessionOnOrAfter(testDate);
        assertEquals("Sunday(08:30:00.000-15:30:00.000)", session.toString());
        
        testDate = new DateTime(2009, 5, 4, 8, 30, 0, 0);
        session = tradingWeek.getTradingSessionOnOrAfter(testDate);
        assertEquals("Monday(08:30:00.000-15:30:00.000)", session.toString());
        
        testDate = new DateTime(2009, 5, 5, 8, 30, 0, 0);
        session = tradingWeek.getTradingSessionOnOrAfter(testDate);
        assertEquals("Tuesday(08:30:00.000-15:30:00.000)", session.toString());
        
        testDate = new DateTime(2009, 5, 6, 8, 30, 0, 0);
        session = tradingWeek.getTradingSessionOnOrAfter(testDate);
        assertEquals("Wednesday(08:30:00.000-15:30:00.000)", session.toString());
        
        testDate = new DateTime(2009, 5, 7, 8, 30, 0, 0);
        session = tradingWeek.getTradingSessionOnOrAfter(testDate);
        assertEquals("Thursday(08:30:00.000-15:30:00.000)", session.toString());
        
        testDate = new DateTime(2009, 5, 8, 8, 30, 0, 0);
        session = tradingWeek.getTradingSessionOnOrAfter(testDate);
        assertEquals("Friday(08:30:00.000-15:30:00.000)", session.toString());
        
        testDate = new DateTime(2009, 5, 9, 8, 30, 0, 0);
        session = tradingWeek.getTradingSessionOnOrAfter(testDate);
        assertEquals("Saturday(08:30:00.000-15:30:00.000)", session.toString());
        
        //////////////////////////////////////////////////////////
        //SECOND: Test advancement to next session
        //Advancement due to hours
        testDate = new DateTime(2009, 5, 2, 16, 30, 0, 0); //Saturday, May 2nd
        session = tradingWeek.getTradingSessionOnOrAfter(testDate);
        assertEquals("Sunday(08:30:00.000-15:30:00.000)", session.toString());
        
        //Advancement due to minutes
        testDate = new DateTime(2009, 5, 2, 15, 31, 0, 0);
        session = tradingWeek.getTradingSessionOnOrAfter(testDate);
        assertEquals("Sunday(08:30:00.000-15:30:00.000)", session.toString());
        
        //Advancement due to seconds
        testDate = new DateTime(2009, 5, 2, 15, 30, 1, 0);
        session = tradingWeek.getTradingSessionOnOrAfter(testDate);
        assertEquals("Sunday(08:30:00.000-15:30:00.000)", session.toString());
    }
    
    /**
     * Test that the DateTime input is advanced correctly for each
     * PeriodType.
     */
    @Test
    public void testGetNextSessionDate() {
        TradingWeek tradingWeek = getTestTradingWeek(TEST);
        
        DateTime testDate = new DateTime(2009, 5, 1, 0, 0, 0, 999);//Friday, May 1st
        
        Period tu = new Period(PeriodType.YEAR, 1);
        DateTime nextDate = tradingWeek.getNextSessionDate(testDate, tu);
        assertEquals("2010-01-01T08:30:00.000", noZone(nextDate.toString()));
        
        tu = new Period(PeriodType.QUARTER, 1);
        nextDate = tradingWeek.getNextSessionDate(testDate, tu);
        assertEquals("2009-07-01T08:30:00.000", noZone(nextDate.toString()));
        
        tu = new Period(PeriodType.MONTH, 1);
        nextDate = tradingWeek.getNextSessionDate(testDate, tu);
        assertEquals("2009-06-01T08:30:00.000", noZone(nextDate.toString()));
        
        tu = new Period(PeriodType.WEEK, 1);
        nextDate = tradingWeek.getNextSessionDate(testDate, tu);
        assertEquals("2009-05-10T08:30:00.000", noZone(nextDate.toString()));
        
        tu = new Period(PeriodType.DAY, 1);
        nextDate = tradingWeek.getNextSessionDate(testDate, tu);
        assertEquals("2009-05-02T08:30:00.000", noZone(nextDate.toString()));
        
        testDate = new DateTime(2009, 5, 1, 8, 30, 0, 999);
        tu = new Period(PeriodType.HOUR, 1);
        nextDate = tradingWeek.getNextSessionDate(testDate, tu);
        assertEquals("2009-05-01T09:30:00.000", noZone(nextDate.toString()));
        
        tu = new Period(PeriodType.MINUTE, 1);
        nextDate = tradingWeek.getNextSessionDate(testDate, tu);
        assertEquals("2009-05-01T08:31:00.000", noZone(nextDate.toString()));
        
        tu = new Period(PeriodType.SECOND, 1);
        nextDate = tradingWeek.getNextSessionDate(testDate, tu);
        assertEquals("2009-05-01T08:30:01.000", noZone(nextDate.toString()));
        
        //Test advancement to following week
        testDate = new DateTime(2009, 5, 1, 0, 0, 0, 999);//Friday, May 1st
        tradingWeek = getTestTradingWeek(TEST3); //Trading Week includes only Tue, Wed, Thur
        tu = new Period(PeriodType.DAY, 1);
        nextDate = tradingWeek.getNextSessionDate(testDate, tu);
        assertEquals("2009-05-05T08:30:00.000", noZone(nextDate.toString()));
        
    }
    
    @Test
    public void testGetMillisBetween() {
        TradingWeek week = getTestTradingWeek(TEST);
        
        //Test comparison convenience variables
        long millisInHour = 3600000;
        long sessionMillis = millisInHour * 7;
        long weekMillis = sessionMillis * 7;
        
        //////////////////// Test within a single session
        DateTime dt1 = new DateTime(2009, 5, 1, 8, 30, 0, 0);
        DateTime dt2 = new DateTime(2009, 5, 1, 9, 30, 0, 0);
        long millis = week.getSessionMillisBetween(dt1, dt2);
        assertEquals(millisInHour, millis);
        
        dt1 = new DateTime(2009, 5, 1, 8, 30, 0, 0);
        dt2 = new DateTime(2009, 5, 1, 15, 30, 0, 0);
        millis = week.getSessionMillisBetween(dt1, dt2);
        assertEquals(millisInHour * 7, millis);
        
        //////////////////// Test dates spanning more than 1 session
        dt1 = new DateTime(2009, 5, 1, 8, 30, 0, 0);
        dt2 = new DateTime(2009, 5, 3, 8, 30, 0, 0);
        millis = week.getSessionMillisBetween(dt1, dt2);
        assertEquals(millisInHour * 14, millis);
        
        dt1 = new DateTime(2009, 5, 1, 8, 30, 0, 0);
        dt2 = new DateTime(2009, 5, 3, 13, 30, 0, 0);
        millis = week.getSessionMillisBetween(dt1, dt2);
        assertEquals(millisInHour * 19, millis);
        
        //////////////////// Test dates spanning a week or more
        //Test 1 week
        dt1 = new DateTime(2009, 5, 1, 8, 30, 0, 0);
        dt2 = new DateTime(2009, 5, 8, 8, 30, 0, 0);
        millis = week.getSessionMillisBetween(dt1, dt2);
        assertEquals(weekMillis, millis);
        
        //Test 2 weeks
        dt1 = new DateTime(2009, 5, 1, 8, 30, 0, 0);
        dt2 = new DateTime(2009, 5, 15, 8, 30, 0, 0);
        millis = week.getSessionMillisBetween(dt1, dt2);
        assertEquals(weekMillis * 2, millis);
        
        //Test 4 weeks
        dt1 = new DateTime(2009, 5, 1, 8, 30, 0, 0);
        dt2 = new DateTime(2009, 5, 29, 8, 30, 0, 0);
        millis = week.getSessionMillisBetween(dt1, dt2);
        assertEquals(weekMillis * 4, millis);
        
        //Test 4 weeks and 1 day
        dt1 = new DateTime(2009, 5, 1, 8, 30, 0, 0);
        dt2 = new DateTime(2009, 5, 30, 8, 30, 0, 0);
        millis = week.getSessionMillisBetween(dt1, dt2);
        long millisInSession = week.getTradingSessionOnOrAfter(dt2).sessionMillis();
        assertEquals((weekMillis * 4) + millisInSession, millis);
        
    }
    
    /**
	 * Rudimentarily removes the zone information appended to the string
	 * @param dateStr
	 * @return
	 */
	private String noZone(String dateStr) {
		return dateStr.substring(0, dateStr.length() - 6);
	}
    
    @Test
    public void testGetTradingDaysInMonth() {
        TradingWeek week = getTestTradingWeek(TEST3);
        DateTime dt1 = new DateTime(2009, 10, 11, 8, 30, 0, 0);
        assertEquals(13, week.getTradingDaysInMonth(dt1));
    }
    
    private static Properties DEFAULT_PROPS; 
    public static TradingWeek DEFAULT;
    private TradingWeek getTestTradingWeek(String symbol) {
        DEFAULT_PROPS = new Properties();
        DEFAULT_PROPS.put("DEFAULT.holidayDateFileLoadType", TradingWeekImpl.LoadType.MEMORY);
        DEFAULT_PROPS.put("DEFAULT.holidayDelimiter", ",");
        DEFAULT_PROPS.put("DEFAULT.holidayDates", "2013-1-1");
        
        DEFAULT_PROPS.setProperty("TEST.sessionParamDelimiter", ",");
        DEFAULT_PROPS.setProperty("TEST.sessionDelimiter", ";");
        DEFAULT_PROPS.setProperty("TEST.sessions", "7,08:30:0:0,7,15:30:0:0;1,08:30:0:0,1,15:30:0:0;2,08:30:0:0,2,15:30:0:0;3,08:30:0:0,3,15:30:0:0;4,08:30:0:0,4,15:30:0:0;5,08:30:0:0,5,15:30:0:0;6,08:30:0:0,6,15:30:0:0");
        
        DEFAULT_PROPS.setProperty("TEST3.sessionParamDelimiter", ",");
        DEFAULT_PROPS.setProperty("TEST3.sessionDelimiter", ";");
        DEFAULT_PROPS.setProperty("TEST3.sessions", "2,08:30:0:0,2,15:30:0:0;3,08:30:0:0,3,15:30:0:0;4,08:30:0:0,4,15:30:0:0");
        
        try {
            DEFAULT = TradingWeekImpl.configBuilder(DEFAULT_PROPS, "DEFAULT", symbol).build();
        } catch(Exception e) { throw new IllegalStateException("could not initialize default trading session."); }
        
        return DEFAULT;
    }
    

}
