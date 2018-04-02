package com.waldo.kicadparser.classes;

public class LibSource {

    private String lib;
    private String part;

    /**
     * Get the library name. If the lib name is null, this getter returns an empty String.
     * @return String value
     */
    public String getLib() {
        if (lib == null) {
            lib = "";
        }
        return lib;
    }

    /**
     * Set the library name.
     * @param lib String value
     */
    public void setLib(String lib) {
        this.lib = lib;
    }

    /**
     * Get the part name. If the part name is null, this getter returns an empty String.
     * @return String value
     */
    public String getPart() {
        if (part == null) {
            part = "";
        }
        return part;
    }

    /**
     * Set the part name.
     * @param part String value
     */
    public void setPart(String part) {
        this.part = part;
    }

}
