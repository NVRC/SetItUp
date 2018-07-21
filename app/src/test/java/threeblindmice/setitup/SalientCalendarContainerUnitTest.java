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
    Calendar calendar, tempCal;
    SalientCalendarContainer sCC;
    Map<Integer,String> dayMap;
    Map<Integer,String> monthMap;
    @Before
    public void setup(){

        //  TODO:  Modify Calendar to test multiple yearly boundary conditions
        calendar = new GregorianCalendar(2018,7,29);
        tempCal = new GregorianCalendar(2018,8,4);
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

        assertEquals(sCC.getLeftDay(),calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(sCC.getKeyFromValue(monthMap,sCC.getLeftMonth()),calendar.get(Calendar.MONTH));
        assertEquals(sCC.getYear(), calendar.get(Calendar.YEAR));

        assertEquals(sCC.getRightDay(),tempCal.get(Calendar.DAY_OF_MONTH));
        assertEquals(sCC.getKeyFromValue(monthMap,sCC.getRightMonth()),tempCal.get(Calendar.MONTH));
        assertEquals(sCC.getYear(), tempCal.get(Calendar.YEAR));

    }

    @Test
    public void SalientCalendarContainer_IncrementWeek_MatchExpectedDate() {
        sCC.incrementWeek();
        calendar = new GregorianCalendar(2018,8,5);
        tempCal = new GregorianCalendar(2018,8,11);
        SalientCalendarContainer_Constructor_MatchCurrentDate();
    }

    @Test
    public void SalientCalendarContainer_DecrementWeek_MatchExpectedDate(){
        sCC.decrementWeek();
        calendar = new GregorianCalendar(2018,7,22);
        tempCal = new GregorianCalendar(2018,7,28);

        SalientCalendarContainer_Constructor_MatchCurrentDate();
        System.out.println("LEFT: "+ sCC.getLeftMonth()+sCC.getLeftDay());
        System.out.println("RIGHT: "+ sCC.getRightMonth()+sCC.getRightDay());

    }

}
