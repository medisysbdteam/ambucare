package co.system.medical.ambucare.dataformate;

/**
 * Created by TahmidH_MIS on 11/30/2016.
 */

public class TParam {
    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String Key;
    public String Value;

    public TParam(String key, String value) {
        Key = key;
        Value = value;
    }
}
