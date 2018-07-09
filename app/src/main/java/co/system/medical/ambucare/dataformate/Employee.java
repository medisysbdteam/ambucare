package co.system.medical.ambucare.dataformate;

/**
 * Created by TahmidH_MIS on 12/12/2016.
 */

public class Employee {
    private String EmpNo;
    private String EmpID;
    private String EmpName;
    private String EmpDept;
    private String EmpSection;
    private String EmpDesignation;
    private String EmpImage;

    public Employee(String empNo, String empName, String empDesignation) {
        EmpNo = empNo;
        EmpName = empName;
        EmpDesignation = empDesignation;
    }

    public Employee() {

    }

    public String getEmpNo() {
        return EmpNo;
    }

    public void setEmpNo(String empNo) {
        EmpNo = empNo;
    }

    public String getEmpID() {
        return EmpID;
    }

    public void setEmpID(String empID) {
        EmpID = empID;
    }

    public String getEmpName() {
        return EmpName;
    }

    public void setEmpName(String empName) {
        EmpName = empName;
    }

    public String getEmpDept() {
        return EmpDept;
    }

    public void setEmpDept(String empDept) {
        EmpDept = empDept;
    }

    public String getEmpSection() {
        return EmpSection;
    }

    public void setEmpSection(String empSection) {
        EmpSection = empSection;
    }

    public String getEmpDesignation() {
        return EmpDesignation;
    }

    public void setEmpDesignation(String empDesignation) {
        EmpDesignation = empDesignation;
    }

    public String getEmpImage() {
        return EmpImage;
    }

    public void setEmpImage(String empImage) {
        EmpImage = empImage;
    }
}
