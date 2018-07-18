package threeblindmice.setitup;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
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
        int offset = 1;
        int maxInMonth = calendar.getMaximum(calendar.get(Calendar.MONTH));
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
        int maxOfMonth = calendar.getMaximum(currMonth);

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
        sCC.incrementWeek();





    }




}
