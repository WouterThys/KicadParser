package com.waldo.kicadparser.classes;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SheetPath {
    private List<String> names = new ArrayList<>();
    private List<Date> tStamps = new ArrayList<>();

    public void parseNames(String names) {
        String[] split = names.split("/");
        this.names = new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty()) {
                this.names.add(s);
            }
        }
    }

    public void parseTimeStamps(String tStamps) {
        String[] split =tStamps.split("/");

        this.tStamps = new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty()) {
                long l = new BigInteger(s, 16).longValue();
                Date date = new Date(l*1000);
                this.tStamps.add(date);
            }
        }
    }


    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<Date> gettStamps() {
        return tStamps;
    }

    public void settStamps(List<Date> tStamps) {
        this.tStamps = tStamps;
    }
}
