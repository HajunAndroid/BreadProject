package kr.co.hajun.breadproject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Example {

    @SerializedName("LOCALDATA_072218_GN")
    @Expose
    private Localdata072218Gn localdata072218Gn;

    public Localdata072218Gn getLocaldata072218Gn() {
        return localdata072218Gn;
    }

    public void setLocaldata072218Gn(Localdata072218Gn localdata072218Gn) {
        this.localdata072218Gn = localdata072218Gn;
    }

}