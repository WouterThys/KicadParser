package com.waldo.kicadparser;

import com.waldo.kicadparser.classes.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        File file = new File("/home/wouter/Documents/Geeken/PowerSupply/PCB/V1/SupplyMainDigital.net");
        KiCadParser parser = new KiCadParser("");

        HashMap<String, List<Component>> map;

        try {
            map = parser.parse(file);

            for (String layer : map.keySet()) {
                System.out.println("- LAYER: " + layer);
                for (Component c : map.get(layer)) {
                    System.out.println(" - " + c);
                }
            }

        } catch (KiCadParser.KiCadParserException e) {
            e.printStackTrace();
        }
    }

}
