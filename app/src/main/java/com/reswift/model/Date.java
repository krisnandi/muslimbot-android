
package com.reswift.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Date {

    @SerializedName("readable")
    @Expose
    private String readable;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    /**
     * 
     * @return
     *     The readable
     */
    public String getReadable() {
        return readable;
    }

    /**
     * 
     * @param readable
     *     The readable
     */
    public void setReadable(String readable) {
        this.readable = readable;
    }

    /**
     * 
     * @return
     *     The timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * 
     * @param timestamp
     *     The timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
