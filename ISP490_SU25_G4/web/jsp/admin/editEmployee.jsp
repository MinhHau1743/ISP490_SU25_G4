<%--
    Document   : editEmployee
    Created on : Jun 14, 2025
    Author     : NGUYEN MINH
   
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

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <%-- Link tới file CSS đã được nâng cấp, chứa style cho cả trang view và edit --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewEmployee.css">
        <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png" type="image/png">
        <script src="https://unpkg.com/feather-icons"></script>
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
                        <form id="editEmployee" action="${pageContext.request.contextPath}/employee?action=edit" method="post" enctype="multipart/form-data">
                            <input type="hidden" name="id" value="${employee.id}">

                            <div class="view-employee-page">

                                <%-- === CẢI TIẾN: PANEL BÊN TRÁI GIỐNG HỆT TRANG VIEW === --%>
                                <div class="avatar-panel">
                                    <div class="avatar-display-box">
                                        <c:url var="avatarUrl" value="/images/default-avatar.png" />
                                        <c:if test="${not empty employee.avatarUrl}">
                                            <c:set var="avatarUrl" value="/${employee.avatarUrl}" />
                                        </c:if>
                                        <img src="${pageContext.request.contextPath}${avatarUrl}" alt="Ảnh đại diện" id="avatarPreview">
                                    </div>

                                    <h2 class="employee-name-title">${employee.lastName} ${employee.middleName} ${employee.firstName}</h2>
                                    <p class="employee-code-title">Mã NV: ${employee.employeeCode}</p>

                                    <input type="file" name="avatar" id="avatarUpload" hidden accept="image/*">
                                    <button type="button" class="btn btn-secondary" id="btnChooseAvatar">Thay đổi ảnh</button>
                                </div>

                                <div class="info-panel">
                                    <div class="info-card">
                                        <h3 class="info-card-title">Thông tin cơ bản</h3>
                                        <div class="info-grid">
                                            <div class="form-group">
                                                <label for="lastName">Họ</label>
                                                <input type="text" id="lastName" name="lastName" value="${employee.lastName}" required>
                                            </div>
                                            <div class="form-group">
                                                <label for="firstName">Tên</label>
                                                <input type="text" id="firstName" name="firstName" value="${employee.firstName}" required>
                                            </div>
                                            <div class="form-group">
                                                <label for="middleName">Tên đệm</label>
                                                <input type="text" id="middleName" name="middleName" value="${employee.middleName}">
                                            </div>
                                            <div class="form-group">
                                                <label for="dateOfBirth">Ngày sinh</label>
                                                <input type="date" id="dateOfBirth" name="dateOfBirth" value="${employee.dateOfBirth}" required>
                                            </div>
                                            <div class="form-group full-width">
                                                <label>Giới tính</label>
                                                <div class="radio-group">
                                                    <label><input type="radio" name="gender" value="male" ${employee.gender == 'male' ? 'checked' : ''}> Nam</label>
                                                    <label><input type="radio" name="gender" value="female" ${employee.gender == 'female' ? 'checked' : ''}> Nữ</label>
                                                    <label><input type="radio" name="gender" value="other" ${employee.gender == 'other' ? 'checked' : ''}> Khác</label>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="info-card">
                                        <h3 class="info-card-title">Thông tin định danh & Liên hệ</h3>
                                        <div class="info-grid">
                                            <%-- === YÊU CẦU: KHÓA CÁC TRƯỜNG NÀY === --%>
                                            <div class="form-group">
                                                <label for="email">Email</label>
                                                <input type="email" id="email" name="email" value="${employee.email}" readonly>
                                            </div>
                                            <div class="form-group">
                                                <label for="phoneNumber">Số điện thoại</label>
                                                <input type="tel" id="phoneNumber" name="phoneNumber" value="${employee.phoneNumber}" readonly>
                                            </div>
                                            <div class="form-group full-width">
                                                <label for="identityCardNumber">Số CMND/CCCD</label>
                                                <input type="text" id="identityCardNumber" name="identityCardNumber" value="${employee.identityCardNumber}" readonly>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="info-card">
                                        <h3 class="info-card-title">Thông tin công việc</h3>
                                        <div class="info-grid">
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
                                </div>
                            </div>

                            <footer class="page-actions-footer">
                                <a href="${pageContext.request.contextPath}/employee?action=view&id=${employee.id}" class="btn btn-secondary" role="button">Hủy</a>
                                <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                            </footer>
                        </form>
                    </c:if>
                </section>
            </main>
        </div>

        <script>feather.replace()</script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
        <script>
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