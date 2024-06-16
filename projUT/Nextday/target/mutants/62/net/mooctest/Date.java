package net.mooctest;

import java.io.PrintStream;

public class Date {
   private Day d;
   private Month m;
   private Year y;

   public Date(int pMonth, int pDay, int pYear) {
      this.y = new Year(pYear);
      this.m = new Month(pMonth, this.y);
      this.d = new Day(pDay, this.m);
   }

   public void increment() {
      if (!this.d.increment()) {
         if (!this.m.increment()) {
            this.y.increment();
            this.m.setMonth(1, this.y);
         }

         this.d.setDay(1, this.m);
      }

   }

   public void printDate() {
      PrintStream var10000 = System.out;
      (new StringBuilder()).append(this.m.getMonth()).append("/").append(this.d.getDay()).append("/").append(this.y.getYear()).toString();
   }

   public Day getDay() {
      return this.d;
   }

   public Month getMonth() {
      return this.m;
   }

   public Year getYear() {
      return this.y;
   }

   public boolean equals(Object o) {
      return o instanceof Date && this.y.equals(((Date)o).y) && this.m.equals(((Date)o).m) && this.d.equals(((Date)o).d);
   }

   public String toString() {
      return this.m.getMonth() + "/" + this.d.getDay() + "/" + this.y.getYear();
   }
}
