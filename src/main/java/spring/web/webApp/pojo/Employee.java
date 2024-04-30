package spring.web.webApp.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @Column(name = "eid")
    private Long eid;

    @Override
    public String toString() {
        return "Employee{" +
                "eId=" + eid +
                ", fName='" + fname + '\'' +
                ", lName='" + lname + '\'' +
                '}';
    }

    @Column(name = "fname")
    private String fname;

    @Column(name = "lname")
    private String lname;

    public Employee(Long eId, String fName, String lName) {
        this.eid = eId;
        this.fname = fName;
        this.lname = lName;
    }

    public Employee() {
    }

    public Long getEid() {
        return eid;
    }

    public void setEid(Long eid) {
        this.eid = eid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }
}
