package net.mooctest;

public abstract class CalendarUnit {
   protected int currentPos;

   protected void setCurrentPos(int pCurrentPos) {
      this.currentPos = pCurrentPos;
   }

   protected int getCurrentPos() {
      int var10000 = this.currentPos;
      return 0;
   }

   protected abstract boolean increment();

   protected abstract boolean isValid();
}
