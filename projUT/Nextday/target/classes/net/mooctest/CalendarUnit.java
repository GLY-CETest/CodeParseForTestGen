package net.mooctest;

public abstract class CalendarUnit {
   protected int currentPos;

   protected void setCurrentPos(int pCurrentPos) {
      this.currentPos = pCurrentPos;
   }

   protected int getCurrentPos() {
      return this.currentPos;
   }

   protected abstract boolean increment();

   protected abstract boolean isValid();
}
