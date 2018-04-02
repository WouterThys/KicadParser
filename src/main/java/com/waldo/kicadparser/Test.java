package com.waldo.kicadparser;

import java.io.File;

public class Test {

    public static void main(String[] args) {
        File file = new File("/home/waldo/Documents/Geeken/Projects/PowerSupply/PCB/SupplyMainDigital.net");
        KiCadParser parser = new KiCadParser("");

        parser.parse(file);
    }

}
