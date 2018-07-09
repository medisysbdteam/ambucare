package co.system.medical.ambucare.auxiliary;

/**
 * Created by TahmidH_MIS on 12/19/2016.
 */

public class StoredProcedure {
    public static String sp_get_emp_list = "usp_M_get_emp_for_auto";
    public static String forward_approval = "sp_M_insert_forward";
    public static String get_po = "usp_SelectProduct";
    public static String set_approval_status = "sp_M_update_approval";
    public static String update_approval_status = "sp_M_po_update";
    public static String get_location_of_other = "sp_M_get_location_of_other";
    public static String set_location = "usp_m_set_location";
    public static String set_location_bulk = "GET_M_LOCATION";
    public static String count_approval_type = "sp_m_get_count_approval_type";
}
