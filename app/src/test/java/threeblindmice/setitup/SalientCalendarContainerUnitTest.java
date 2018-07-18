package threeblindmice.setitup;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import threeblindmice.setitup.util.SalientCalendarContainer;

import static org.junit.Assert.assertEquals;

public class SalientCalendarContainerUnitTest {
    String testMonth = "January";
    int testDay = 1;
    Calendar calendar;
    SalientCalendarContainer sCC;
    Map<Integer,String> dayMap;
    Map<Integer,String> monthMap;
    @Before
    public void setup(){

        //  Modify Calendar to test multiple yearly boundary conditions
        calendar = Calendar.getInstance();

        sCC = new SalientCalendarContainer(calendar);
        dayMap = sCC.generateDayMap();
        monthMap = sCC.generateMonthMap();
    }

    @Test
    public void SalientCalendarContainerSetterGetter_LeftMonthDay_MatchCurrentDate(){
        sCC.setLeftMonthDay(testMonth,testDay);
        assertEquals(sCC.getLeftDay(),testDay);
        assertEquals(sCC.getLeftMonth(),testMonth);
    }

    @Test
    public void SalientCalendarContainerSetterGetter_RightMonthDay_MatchCurrentDate(){
        sCC.setRightMonthDay(testMonth,testDay);
        assertEquals(sCC.getRightDay(),testDay);
        assertEquals(sCC.getRightMonth(),testMonth);
    }

    //  Test Day Map boundaries
    @Test
    public void SalientCalendarContainerUtil_GenerateDayMap_ExpectedMapping(){
        assertEquals(dayMap.get(Calendar.SUNDAY),"Sunday");
        assertEquals(dayMap.get(Calendar.SATURDAY),"Saturday");
    }

    //  Test Month Map boundaries
    @Test
    public void SalientCalendarContainerUtil_GenerateMonthMap_ExpectedMapping(){

        assertEquals(monthMap.get(Calendar.JANUARY),"January");
        assertEquals(monthMap.get(Calendar.DECEMBER), "December");
    }

    @Test
    public void SalientCalendarContainerUtil_CalculateMonthRollover_ExpectedResults(){
        int offset = 3;
        // Create a calendar object and set year and month

        int maxInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dayInMonth = maxInMonth + offset;
        assertEquals(offset,sCC.calculateMonthRollover(dayInMonth,maxInMonth));
    }

    @Test
    public void SalientCalendarContainerUtil_GetKeyFromValue_ExpectedKey(){

        //  Expected Key Mapping 1 <-> Sunday
        Object obj = sCC.getKeyFromValue(dayMap,"Sunday");
        assertEquals(obj,Calendar.SUNDAY);

        //  Expected Key Mapping 12 <-> December
        obj = sCC.getKeyFromValue(monthMap,"December");
        assertEquals(obj,Calendar.DECEMBER);
    }

    @Test
    public void SalientCalendarContainer_Constructor_MatchCurrentDate(){
        int currDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int currMonth = calendar.get(Calendar.MONTH);
        int maxOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        assertEquals(sCC.getLeftDay(),currDayOfMonth);
        assertEquals(sCC.getLeftMonth(),monthMap.get(currMonth));

        int rollover = sCC.calculateMonthRollover(
                currDayOfMonth + SalientCalendarContainer.NUM_WEEKDAYS,maxOfMonth);
        if (rollover < currDayOfMonth){
            currMonth++;
        }

        assertEquals(sCC.getRightDay(),rollover);
        assertEquals(sCC.getRightMonth(),monthMap.get(currMonth));

    }

    @Test
    public void SalientCalendarContainer_IncrementWeek_MatchExpectedDate() {


        int currMonth = (int) sCC.getKeyFromValue(monthMap,sCC.getRightMonth());
        int maxOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currDay = sCC.getRightDay()+1;
        int rollover = sCC.calculateMonthRollover(currDay, maxOfMonth);
        int year = calendar.get(Calendar.YEAR);
        //  Month roll over
        if (rollover < currDay){
            if(currMonth <= 11){
                currMonth++;
            } else {
                currMonth = 0;
                year++;
            }

            Calendar calendar = new GregorianCalendar(year, currMonth, rollover);


        }
        int leftMonth = currMonth;

        //  Add the week
        sCC.incrementWeek();
        int weekAddedDay = currDay + SalientCalendarContainer.NUM_WEEKDAYS;
        maxOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int newDay = sCC.calculateMonthRollover(weekAddedDay, maxOfMonth);
        //  Month roll over
        if (newDay < weekAddedDay){
            if(currMonth <= 11){
                currMonth++;
            } else {
                currMonth = 1;
            }
        }
        assertEquals(sCC.getLeftDay(), rollover);
        assertEquals(sCC.getLeftMonth(), monthMap.get(leftMonth));
        assertEquals(sCC.getRightDay(), newDay);
        assertEquals(sCC.getRightMonth(), monthMap.get(currMonth));
    }

    @Test
    public void SalientCalendarContainer_DecrementWeek_MatchExpectedDate(){
/*
        assertEquals(sCC.getLeftDay(), "");
        assertEquals(sCC.getLeftMonth(), monthMap.get(""));
        assertEquals(sCC.getRightDay(), "");
        assertEquals(sCC.getRightMonth(), monthMap.get(""));

        System.out.println("LEFT: "+ sCC.getLeftMonth()+sCC.getLeftDay());
        System.out.println("RIGHT: "+ sCC.getRightMonth()+sCC.getRightDay());
        */
    }




}
