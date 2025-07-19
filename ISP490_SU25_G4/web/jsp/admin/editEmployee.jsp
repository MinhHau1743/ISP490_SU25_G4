<%--
    Document   : editEmployee
    Created on : Jun 14, 2025
    Author     : NGUYEN MINH
    Purpose    : Edit employee information, consistent with viewEmployee.jsp and database schema.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix = "fmt" %>
<c:set var="currentPage" value="listEmployee" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chỉnh sửa thông tin nhân viên</title>

        <%-- Giữ các link CSS tương tự như trang view để giao diện đồng bộ --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <%-- Sử dụng lại CSS của trang viewEmployee để có layout 2 cột --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewEmployee.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Chỉnh sửa thông tin nhân viên</div>
                </header>

                <section class="content-body">
                    <c:if test="${not empty employee}">
                        <%-- Form sẽ bao bọc toàn bộ khu vực nội dung --%>
                        <form id="editEmployee" action="editEmployee" method="post" enctype="multipart/form-data">

                            <%-- Gửi ID của nhân viên để server biết cập nhật đối tượng nào --%>
                            <input type="hidden" name="id" value="${employee.id}">

                            <div class="view-employee-page"> <%-- Sử dụng lại layout của trang view --%>

                                <%-- Panel bên trái cho ảnh đại diện --%>
                                <div class="avatar-panel">
                                    <div class="avatar-display-box">
                                        <c:url var="avatarUrl" value="/images/default-avatar.png" />
                                        <c:if test="${not empty employee.avatarUrl}">
                                            <c:set var="avatarUrl" value="/${employee.avatarUrl}" />
                                        </c:if>
                                        <img src="${pageContext.request.contextPath}${avatarUrl}" alt="Ảnh đại diện" id="avatarPreview">
                                    </div>
                                    <input type="file" name="avatar" id="avatarUpload" hidden accept="image/*">
                                    <button type="button" class="btn btn-secondary" id="btnChooseAvatar">Thay đổi ảnh</button>
                                </div>

                                <%-- Panel bên phải chứa các card thông tin --%>
                                <div class="info-panel">
                                    <div class="info-card">
                                        <h3 class="info-card-title">Thông tin khởi tạo</h3>
                                        <div class="info-card-grid">
                                            <div class="form-group"><label>Mã nhân viên</label><input type="text" value="${employee.employeeCode}" disabled></div>
                                            <div class="form-group"><label for="lastName">Họ</label><input type="text" id="lastName" name="lastName" value="${employee.lastName}"></div>
                                            <div class="form-group"><label for="middleName">Tên đệm</label><input type="text" id="middleName" name="middleName" value="${employee.middleName}"></div>
                                            <div class="form-group"><label for="firstName">Tên</label><input type="text" id="firstName" name="firstName" value="${employee.firstName}"></div>
                                            <div class="form-group"><label for="phoneNumber">Số điện thoại</label><input type="tel" id="phoneNumber" name="phoneNumber" value="${employee.phoneNumber}"></div>
                                            <div class="form-group"><label for="email">Email</label><input type="email" id="email" name="email" value="${employee.email}" disabled></div>
                                        </div>
                                    </div>

                                    <div class="info-card">
                                        <h3 class="info-card-title">Thông tin công việc</h3>
                                        <div class="info-card-grid">
                                            <div class="form-group">
                                                <label for="departmentId">Phòng làm việc</label>
                                                <select id="departmentId" name="departmentId">
                                                    <c:forEach var="dept" items="${departments}">
                                                        <option value="${dept.id}" ${employee.departmentId == dept.id ? 'selected' : ''}>${dept.name}</option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                            <div class="form-group">
                                                <label for="positionId">Chức vụ</label>
                                                <select id="positionId" name="positionId">
                                                    <c:forEach var="pos" items="${positions}">
                                                        <option value="${pos.id}" ${employee.positionId == pos.id ? 'selected' : ''}>${pos.name}</option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="form-group full-width">
                                            <label for="notes">Ghi chú</label>
                                            <textarea id="notes" name="notes" rows="3">${employee.notes}</textarea>
                                        </div>
                                    </div>

                                    <div class="info-card">
                                        <h3 class="info-card-title">Thông tin cá nhân</h3>
                                        <div class="info-card-grid">
                                            <div class="form-group">
                                                <label for="identityCardNumber">Số CMND/CCCD</label>
                                                <input type="text" id="identityCardNumber" name="identityCardNumber" value="${employee.identityCardNumber}">
                                            </div>
                                            <div class="form-group">
                                                <label for="dateOfBirth">Ngày sinh</label>
                                             
                                                <input type="date" id="dateOfBirth" name="dateOfBirth" value="${employee.dateOfBirth}">
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label>Giới tính</label>
                                            <div class="radio-group">
                                                <label><input type="radio" name="gender" value="male" ${employee.gender == 'male' ? 'checked' : ''}> Nam</label>
                                                <label><input type="radio" name="gender" value="female" ${employee.gender == 'female' ? 'checked' : ''}> Nữ</label>
                                                <label><input type="radio" name="gender" value="other" ${employee.gender == 'other' ? 'checked' : ''}> Khác</label>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <footer class="page-actions-footer">
                                <a href="${pageContext.request.contextPath}/viewEmployee?id=${employee.id}" class="btn btn-secondary" role="button">Hủy</a>
                                <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                            </footer>
                        </form>
                    </c:if>

                    <c:if test="${empty employee}">
                        <p>Không tìm thấy thông tin nhân viên để chỉnh sửa.</p>
                    </c:if>
                </section>
            </main>
        </div>

        <script src="https://unpkg.com/feather-icons"></script>
        <script>feather.replace()</script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
        <script>
            // JavaScript đơn giản để xử lý việc chọn ảnh
            document.getElementById('btnChooseAvatar').addEventListener('click', function () {
                document.getElementById('avatarUpload').click();
            });

            document.getElementById('avatarUpload').addEventListener('change', function (event) {
                const [file] = event.target.files;
                if (file) {
                    document.getElementById('avatarPreview').src = URL.createObjectURL(file);
                }
            });
        </script>
    </body>
</html>