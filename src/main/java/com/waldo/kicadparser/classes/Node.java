package com.waldo.kicadparser.classes;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private List<Node> childNodes;

    public String name = "";
    public String value = "";

    public int parseLength = 0;

    public Node() {
        childNodes = new ArrayList<>();
    }

    public void addChild(Node child) {
        childNodes.add(child);
    }

    public List<Node> getChildren() {
        return childNodes;
    }

}
