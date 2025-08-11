package vn.edu.fpt.model;

/**
 * Lớp Model này đại diện cho một loại chiến dịch (Campaign Type). Tương ứng với
 * bảng 'CampaignTypes' trong cơ sở dữ liệu.
 */
public class CampaignType {

    private int id;
    private String typeName;

    /**
     * Constructor mặc định.
     */
    public CampaignType() {
    }

    /**
     * Constructor đầy đủ tham số.
     *
     * @param id Mã định danh duy nhất của loại chiến dịch.
     * @param typeName Tên của loại chiến dịch (ví dụ: "Tri ân và ưu đãi khách
     * hàng").
     */
    public CampaignType(int id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Phương thức toString() để dễ dàng gỡ lỗi.
     *
     * @return Chuỗi đại diện cho đối tượng CampaignType.
     */
    @Override
    public String toString() {
        return "CampaignType{"
                + "id=" + id
                + ", typeName='" + typeName + '\''
                + '}';
    }
}
