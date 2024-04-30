package spring.web.webApp.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Employee {
    @Id
    @Column
    private Long eId;

    @Column
    private String fName;

    @Column
    private String lName;

    public Employee(Long eId, String fName, String lName) {
        this.eId = eId;
        this.fName = fName;
        this.lName = lName;
    }

    public Employee() {
    }

    public Long geteId() {
        return eId;
    }

    public void seteId(Long eId) {
        this.eId = eId;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }
}
