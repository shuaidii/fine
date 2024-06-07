package labtest;

import java.util.Vector;

/* Missing lowercase test on password */

public class DateCalculator {

	private String id="201501353";
	private String name="Sun, Jihan";
	private String args="int day, int month,int year";

			
	private int currentDay,currentMonth,currentYear;		// current date
	
	private static final int JANUARY=1;
	private static final int FEBRUARY=2;
	private static final int DECEMBER=12;
	
	private boolean isLeapYear(int year) {
		if ( (year % 4) !=0) {
			return(false);
		}
		if ((year % 400)==0) {
			return(true);
		}
		if ( (year % 100)==0) {
			return(false);
		}
		return(true);
	}
	
	                                 //     JAN  FEB MAR APR MAY JUN JUL AUG SEP OCT NOV DEC
	private static final int daysInMonth[]= {31  ,28, 31 ,30 ,31 ,30 ,31 ,31, 30 ,31, 30, 31 };

	private static final int DAYS_IN_YEAR = 365;
	
	/**
	 * Calculate days in month with adjustment for leap year
	 * @param year
	 * @param month
	 * @return
	 */
	private int daysInMonth(int year,int month) {
		int daysInMonth=DateCalculator.daysInMonth[month-1];
		if ( (month==FEBRUARY) && (isLeapYear(year)) ) {
			daysInMonth++;	// add in leap day
		}
		return(daysInMonth);
	}
	
	private void validateArguments(int day,int month,int year) {
		if ( (month<1) || (month>12)) {
			throw new BadDateException();
		}
		if (year<0) {
			throw new BadDateException();
		}
		if (day<1) {
			throw new BadDateException();
		}
		int daysInMonth=daysInMonth(year,month);
		if (day>daysInMonth) {
			throw new BadDateException();
		}
	}
	
	public void setCurrentDate(int day, int month,int year) {
		validateArguments(day,month,year);
		this.currentDay=day;
		this.currentYear=year;
		this.currentMonth=month;
	}
	
	
	/**
	 * Calculates the number of days until the date given
	 * To test this code, you can set the current date, using
	 * the setCurrentDate test method
	 * If the date is in the past, the calculation will return a negative value
	 */
	public int calculateDaysTill(int day, int month,int year) {
		validateArguments(day,month,year);
		int dayCount=0;		// sum of days till the day
		if (year>=this.currentYear) {  // date in future or now
			int y=currentYear;
			while (y<year) { // first add on the whole years
				if (currentMonth>month) {
					if (y==year-1) { // break out when less than 12 months to go
						break;
					}
				}
				dayCount+=DAYS_IN_YEAR;
				if (month<=FEBRUARY) {
					if (isLeapYear(y)) {
						dayCount++;
					} 
				} else {
					if (isLeapYear(y+1)) {
						dayCount++;
					}					
				}
				y++;				
			}
			// we now add in the month counts
			int m=currentMonth;
			while (m!=month) {
				dayCount+=this.daysInMonth(y,m);
				m++;
				if (m>DECEMBER)  {
					m=JANUARY;
					y++;
				}
			}
			System.out.println("Day count is "+dayCount);
			System.out.println("Day is "+day);
			System.out.println("current is "+this.currentDay);
			
			
			dayCount=dayCount+(day-this.currentDay);
			
		} else {
			int y=currentYear;
			while (y>year) { // first take of the whole years
				if (currentMonth<month) {
					if (y==year+1) { // break out when less than 12 months to go
						break;
					}
				}
				dayCount-=DAYS_IN_YEAR;
				if (month>FEBRUARY) {
					if (isLeapYear(y)) {
						dayCount--; // take of leap day
					} 
				} else {
					if (isLeapYear(y-1)) {
						dayCount--;
					}					
				}
				y--;				
			}
			// we now add in the month counts
			int m=currentMonth;
			while (m!=month) {
				dayCount-=this.daysInMonth(y,m);
				m--;
				if (m<JANUARY)  {
					m=DECEMBER;
					y--;
				}
			}
			dayCount=dayCount+(day-this.currentDay);

			
			
		}
		return(dayCount);
	}
	
	
}
