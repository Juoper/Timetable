package org.timeTable.models.json;

import java.time.Period;

public class ElementPeriod {
    public int startTime;
    public int endTime;
    public PeriodElement[] elements;
    public String cellState;

    public int getId(){
        return elements[1].getId();
    }

    public String getCellState() {
        return cellState;
    }
}

class PeriodElement {
    int id;

    public int getId() {
        return id;
    }
}