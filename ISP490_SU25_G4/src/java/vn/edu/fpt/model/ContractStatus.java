package vn.edu.fpt.model;

/**
 * Lớp Model ánh xạ tới bảng `contract_statuses`.
 *
 * @author AI
 */
public class ContractStatus {

    private int id;
    private String name;

    public ContractStatus() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
