<%@page contentType="text/html" pageEncoding="UTF-8"%>
<form action="ProductCategories" method="post">
    <div class="modal fade" id="addGroupModal" tabindex="-1" role="dialog" aria-labelledby="addGroupLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">

                <div class="modal-header">
                    <h5 class="modal-title" id="addGroupLabel">Thêm nhóm sản phẩm</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Đóng">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>

                <div class="modal-body">
                    <input type="text" class="form-control" name="groupName" id="groupNameInput" placeholder="Tên nhóm sản phẩm" required>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-primary">Thêm và đóng</button>
                </div>

            </div>
        </div>
    </div>
</form>