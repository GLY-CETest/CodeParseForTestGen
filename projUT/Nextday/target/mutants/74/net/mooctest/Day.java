package net.mooctest;

public class Day extends CalendarUnit {
   private Month m;

   public Day(int pDay, Month m) {
      this.setDay(pDay, m);
   }

   public boolean increment() {
      ++this.currentPos;
      if (this.currentPos <= this.m.getMonthSize()) {
         boolean var10000 = true;
         return false;
      } else {
         return false;
      }
   }

   public void setDay(int pDay, Month m) {
      this.setCurrentPos(pDay);
      this.m = m;
      if (!this.isValid()) {
         throw new IllegalArgumentException("Not a valid day");
      }
   }

   public int getDay() {
      return this.currentPos;
   }

   public boolean isValid() {
      return this.m != null && this.m.isValid() && this.currentPos >= 1 && this.currentPos <= this.m.getMonthSize();
   }

   public boolean equals(Object o) {
      return o instanceof Day && this.currentPos == ((Day)o).currentPos && this.m.equals(((Day)o).m);
   }
}
