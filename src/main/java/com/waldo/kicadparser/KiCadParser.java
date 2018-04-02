package com.waldo.kicadparser;

import com.waldo.kicadparser.classes.Component;
import com.waldo.kicadparser.classes.LibSource;
import com.waldo.kicadparser.classes.Node;
import com.waldo.kicadparser.classes.SheetPath;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class KiCadParser {

    private String parserName;
    private File fileToParse;

    private String fileExtension;
    private String fileStartSequence;
    private String fileStopSequence;

    //private List<Component> componentList;
    private HashMap<String, List<Component>> componentMap; // List per sheet
    private boolean hasParsed;

    public KiCadParser(String parserName) {
        this.parserName = parserName;
        this.fileExtension = "net";
        this.fileStartSequence = "(components";
        this.fileStopSequence = "(libparts";

        //componentList = new ArrayList<>();
        componentMap = new HashMap<>();
        hasParsed = false;
    }

    public HashMap<String, List<Component>> getParsedData() {
        if (!hasParsed) {
            parse(fileToParse);
        }
        for (String key : componentMap.keySet()) {
            Collections.sort(componentMap.get(key), new KiCadComponentComparator());
        }

        return componentMap;
    }

    @Override
    public String toString() {
        return parserName;
    }

    public boolean isFileValid(File fileToParse) {
        boolean fileExists = fileToParse.exists();
        boolean extensionOk = getExtension(fileToParse).equals(fileExtension);
        return fileExists && extensionOk;
    }

    public String getParserName() {
        return parserName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public List<Component> sortList(List<Component> list) {
        List<Component> sorted = new ArrayList<>();
        if (list != null) {
            for (Component component : list) {
                if (sorted.contains(component)) {
                    sorted.get(sorted.indexOf(component)).addReference(component.getRef());
                } else {
                    sorted.add(component);
                }
            }
        }
        return sorted;
    }

    public void parse(File fileToParse) {
        this.fileToParse = fileToParse;
        if (isFileValid(fileToParse)) {
            String fileData = getRawStringFromFile(fileToParse);
            int startNdx = fileData.indexOf(fileStartSequence);
            int stopNdx = fileData.indexOf(fileStopSequence);

            String usefulData = fileData.substring(startNdx, stopNdx);

            try {
                String block = readBlock(usefulData);
                Node head = parseBlock(block);
                componentMap.clear();
                componentMap = parseNode(head);
                hasParsed = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getRawStringFromFile(File file) {
        StringBuilder result = new StringBuilder();
        if (file != null) {
            if (file.exists()) {
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line).append("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return result.toString();
    }

    private String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }

    private HashMap<String, List<Component>> parseNode(Node head) {
        HashMap<String, List<Component>> components = new HashMap<>();
        for (Node n1 : head.getChildren()) { // n1 has the list with all the components
            if (n1.name.equals("comp")) {

                Component component = new Component();

                for (Node n2 : n1.getChildren()) {
                    switch (n2.name) {
                        case "ref": component.setRef(n2.value); break;
                        case "value": component.setValue(n2.value.replaceAll("\"", "")); break;
                        case "footprint": component.setFootprint(n2.value); break;
                        case "libsource":
                            LibSource libSource = new LibSource();
                            for (Node n3 : n2.getChildren()) {
                                switch (n3.name) {
                                    case "lib": libSource.setLib(n3.value); break;
                                    case "part": libSource.setPart(n3.value); break;
                                    default:break;
                                }
                            }
                            component.setLibSource(libSource);
                            break;
                        case "sheetpath":
                            SheetPath sheetPath = new SheetPath();
                            for (Node n4: n2.getChildren()) {
                                switch (n4.name) {
                                    case "names": sheetPath.parseNames(n4.value); break;
                                    case "tstamps": sheetPath.parseTimeStamps(n4.value); break;
                                    default:break;
                                }
                            }
                            component.setSheetPath(sheetPath);
                            break;
                        case "tstamp": component.parseTimeStamp(n2.value); break;
                        default:break;
                    }
                }

                List<String> keys = new ArrayList<>();
                if(component.getSheetPath().getNames().size() == 0) {
                    keys.add("Main");
                } else {
                    keys.addAll(component.getSheetPath().getNames());
                }

                for (String key : keys) {
                    if (!components.containsKey(key)) {
                        components.put(key, new ArrayList<Component>());
                    }
                    components.get(key).add(component);
                }
            }
        }
        return components;
    }

    private Node parseBlock(String block) {
        // Remove first and last bracket
        block = block.substring(1, block.length()-1);

        // Node
        Node headNode = new Node();
        StringBuilder name = new StringBuilder();
        StringBuilder value = new StringBuilder();
        char[] blockChars = block.toCharArray();

        boolean valueStart = false;
        boolean nameEnd = false;
        boolean endFound = false;
        int charCnt = 0;

        while(!endFound) {

            char c = blockChars[charCnt];
            if (c == '(') {
                // new Block
                String newData;
                newData = new String(blockChars).substring(charCnt, blockChars.length);
                String newBlock = readBlock(newData);
                Node child = parseBlock(newBlock);
                headNode.addChild(child);
                charCnt += child.parseLength;
            } else {

                if (!Character.isLetterOrDigit(c) && !nameEnd) {
                    nameEnd = true;
                }

                if ((Character.isLetterOrDigit(c) || c == '/') && nameEnd) {
                    valueStart = true;
                }

                if (!nameEnd) {
                    name.append(blockChars[charCnt]);
                }
                if (valueStart) {
                    value.append(blockChars[charCnt]);
                }
            }

            endFound = (charCnt == blockChars.length-1);
            charCnt++;
        }
        headNode.name = name.toString();
        headNode.value = value.toString();
        headNode.parseLength = charCnt;
        return headNode;
    }

    private String readBlock(String data) {
        StringBuilder block = new StringBuilder();
        char[] chars = data.toCharArray();
        boolean endFound = false;
        boolean startFound = false;
        int charCnt = 0;
        int bracketCnt = 0;

        while(!endFound) {
            if (chars[charCnt] == '(') {
                bracketCnt++;
                startFound = true;
            }
            if (chars[charCnt] == ')') {
                bracketCnt--;
            }

            if (startFound) {
                block.append(chars[charCnt]);
            }

            endFound = (bracketCnt == 0) || (charCnt == data.length());
            charCnt++;
        }
        return block.toString();
    }

    private class KiCadComponentComparator implements Comparator<Component> {
        @Override
        public int compare(Component o1, Component o2) {
            try {
                return o1.getLibSource().getPart().compareTo(o2.getLibSource().getPart());
            } catch(Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    public List<Component> createUniqueList(List<Component> componentList) {
        List<Component> uniqueList = new ArrayList<>();
        for (Component comp : componentList) {
            if (!uniqueContains(uniqueList, comp)) {
                uniqueList.add(comp);
            }
        }
        return uniqueList;
    }

    private boolean uniqueContains(List<Component> componentList, Component component) {
        for (Component comp : componentList) {
            if (comp.getValue().equals(component.getValue()) &&
                    comp.getFootprint().equals(component.getFootprint()) &&
                    comp.getLibSource().getLib().equals(component.getLibSource().getLib()) &&
                    comp.getLibSource().getPart().equals(component.getLibSource().getPart())) {
                return true;
            }
        }
        return false;
    }

}

