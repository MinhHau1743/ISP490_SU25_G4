package vn.edu.fpt.model;

/**
 * Lớp này đại diện cho một Vai trò trong hệ thống.
 * Tương ứng với bảng 'Roles' trong cơ sở dữ liệu.
 *
 * @author PC
 */
public class Role {
    
    // Thuộc tính tương ứng với cột 'id'
    private int id;
    
    // Thuộc tính tương ứng với cột 'name'
    private String name;

    /**
     * Constructor mặc định.
     */
    public Role() {
    }

    /**
     * Constructor đầy đủ tham số.
     * @param id ID của vai trò
     * @param name Tên của vai trò (ví dụ: "Admin", "CSKH")
     */
    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // --- GETTERS AND SETTERS ---
    
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

    @Override
    public String toString() {
        return "Role{" + "id=" + id + ", name=" + name + '}';
    }
}