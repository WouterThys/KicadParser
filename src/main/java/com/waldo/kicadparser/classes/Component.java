package com.waldo.kicadparser.classes;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Component {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private String ref;
    private String value;
    private String footprint;
    private LibSource libSource;
    private SheetPath sheetPath;
    private Date tStamp;
    private List<String> references;

    public Component() {
        libSource = new LibSource();
        sheetPath = new SheetPath();
    }

    @Override
    public String toString() {
        return getRef() + ", " + getValue() + ", " + getFootprint();
    }

    public void parseTimeStamp(String tStamp) {
        if (!tStamp.isEmpty()) {
            long l = new BigInteger(tStamp, 16).longValue();
            this.tStamp = new Date(l*1000);
        }
    }

    public static DateFormat getDateFormat() {
        return dateFormat;
    }

    public String getRef() {
        if (ref == null)  {
            ref = "";
        }
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getValue() {
        if (value == null) {
            value = "";
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFootprint() {
        if (footprint == null) {
            footprint = "";
        }
        return footprint;
    }

    public void setFootprint(String footprint) {
        this.footprint = footprint;
    }

    public LibSource getLibSource() {
        return libSource;
    }

    public void setLibSource(LibSource libSource) {
        this.libSource = libSource;
    }

    public SheetPath getSheetPath() {
        return sheetPath;
    }

    public void setSheetPath(SheetPath sheetPath) {
        this.sheetPath = sheetPath;
    }

    public Date gettStamp() {
        return tStamp;
    }

    public void settStamp(Date tStamp) {
        this.tStamp = tStamp;
    }

    public List<String> getReferences() {
        if (references == null) {
            references = new ArrayList<>();
            references.add(getRef());
        }
        return references;
    }

    public void addReference(String reference) {
        if (!getReferences().contains(reference)) {
            getReferences().add(reference);
        }
    }



}
