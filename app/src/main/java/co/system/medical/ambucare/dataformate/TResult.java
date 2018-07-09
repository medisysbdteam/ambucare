package co.system.medical.ambucare.dataformate;

import org.json.JSONArray;

/**
 * Created by TahmidH_MIS on 11/30/2016.
 */

public class TResult {
    private String ID;
    private JSONArray data;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public JSONArray getData() {
        return data;
    }

    public void setData(JSONArray data) {
        this.data = data;
    }
}
