package threeblindmice.setitup.util;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SalientCalendarContainer {
    //  Constants
    public final static int NUM_WEEKS_CACHED = 10;
    public final static int NUM_WEEKDAYS = 7;
    public final static boolean FUTURE = false;
    public final static boolean PAST = true;

    private String leftMonth;
    private String rightMonth;
    private int leftDay;
    private int rightDay;
    private int[][] weekArray;
    private Calendar calendar;
    private String[] dayArray;
    //  TODO: Implement day suffix logic here

    public SalientCalendarContainer(){
        leftMonth = null;
        rightMonth = null;
        leftDay = 0;
        rightDay = 0;
        weekArray = new int[NUM_WEEKS_CACHED][7];
        //  TODO: Handle date expression properly
        calendar = Calendar.getInstance();
        dayArray = new String[NUM_WEEKDAYS];
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int currMonth = calendar.get(Calendar.MONTH);

        buildVariables(currMonth,dayOfMonth,daysInMonth, FUTURE);

    }

    private void buildVariables(int currMonth, int dayOfMonth, int daysInMonth, boolean directionInTime){
        Map<Integer, String> dayMap = generateDayMap();
        Map<Integer, String> monthMap = generateMonthMap();

        String monthString = monthMap.get(currMonth);
        setLeftMonthDay(monthString, dayOfMonth);
        int firstDayNextWeek = dayOfMonth + NUM_WEEKDAYS;
        if( firstDayNextWeek > daysInMonth){
            int diff = firstDayNextWeek - daysInMonth;
            setRightMonthDay(monthMap.get(currMonth + 1),diff);
        } else {
            setRightMonthDay(monthString, firstDayNextWeek);
        }

        //  TODO: Populate sCC with additional weeks
        int i = 0;
        int dayOfWeek = dayOfMonth%7;
        if (directionInTime == FUTURE){
            System.out.println("\t\tFuture\t\t Day Of Week: "+dayOfWeek + "\t\t Day of Month: "+dayOfMonth);
            for (int j = dayOfWeek; j < dayOfWeek + NUM_WEEKDAYS; j++) {
                if (i >= NUM_WEEKDAYS){
                    break;
                }
                if(j > NUM_WEEKDAYS){
                    j = 1;
                }
                //  Display (Today) on the first Day
                dayArray[i] = ( i == 0 ) ? "Today "+"("+ dayMap.get(j) +")" : dayMap.get(j) ;
                i++;
            }
            for (String day : dayArray){
                System.out.println(day);
            }
        } else if (directionInTime == PAST){
            int temp = calendar.get(Calendar.DAY_OF_WEEK);
            for (int j = temp; j < 8; j--) {
                if (i >= NUM_WEEKDAYS){
                    break;
                }
                if(j == 1){
                    j = 7;
                }
                //  Display (Today) on the first Day
                dayArray[i] = ( i == 0 ) ? "Today "+"("+ dayMap.get(j) +")" : dayMap.get(j) ;
                i++;
            }
        }
    }

    public void incrementWeek(){
        Map<Integer, String> dayMap = generateDayMap();
        Map<Integer, String> monthMap = generateMonthMap();
        int currMonth = (int) getKeyFromValue(monthMap, getRightMonth());
        System.out.println("\t\tRight Day: "+getRightDay());
        buildVariables(currMonth,
                rightDay,
                calendar.get(currMonth), FUTURE);
    }

    public void decrementWeek(){
        Map<Integer, String> dayMap = generateDayMap();
        Map<Integer, String> monthMap = generateMonthMap();
        int currMonth = (int) getKeyFromValue(monthMap, getLeftMonth());
        buildVariables(currMonth,
                getLeftDay(),
                calendar.get(currMonth), PAST);

    }

    public void setLeftMonthDay(String month, int day){
        leftMonth = month;
        leftDay = day;
    }

    public void setRightMonthDay(String month, int day){
        rightMonth = month;
        rightDay = day;
    }

    public String getLeftMonth(){
        return leftMonth;
    }
    public int getLeftDay(){
        return leftDay;
    }

    public String getRightMonth(){
        return rightMonth;
    }

    public int getRightDay(){
        return rightDay;
    }

    public String[] getDayArray(){
        return dayArray;
    }

    //  Helper Functions
    private Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }


    private Map<Integer, String> generateDayMap(){
        //  TODO: Optimize by using SparseIntArray to avoid auto-boxing int to Integer
        Map<Integer, String> dayMap = new HashMap<>();
        DateFormatSymbols dfs = new DateFormatSymbols();
        int i = 0;
        for(String day: dfs.getWeekdays()){
            dayMap.put(i,day);
            i++;
        }
        return dayMap;
    }

    private Map<Integer, String> generateMonthMap(){
        //  TODO: Optimize by using SparseIntArray to avoid auto-boxing int to Integer
        Map<Integer, String> monthMap = new HashMap<>();
        DateFormatSymbols dfs = new DateFormatSymbols();
        int i = 0;
        for(String day: dfs.getMonths()){
            monthMap.put(i,day);
            i++;
        }
        return monthMap;
    }

}
