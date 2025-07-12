<%-- 
    Document   : editProfile.jsp
    Created on : Jun 5, 2025, 9:27:00 PM
    Author     : minhnhn (đã được AI cập nhật)
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="BASE_URL" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chỉnh sửa thông tin cá nhân</title>

        <%-- Các link CSS và script giữ nguyên --%>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="stylesheet" href="css/style.css">
        <link rel="stylesheet" href="css/header.css">
        <link rel="stylesheet" href="css/mainMenu.css">
        <link rel="stylesheet" href="css/profile.css">

    </head>
    <body>

        <div class="app-container">
            <c:if test="${empty userID}">
                <jsp:include page="mainMenu.jsp"/>
            </c:if>
            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Chỉnh sửa thông tin chi tiết</div>
                    <%-- Các actions header giữ nguyên --%>
                </header>

                <div class="profile-form-container">                      
                    <form id="editProfileForm" action="editProfile" method="post" enctype="multipart/form-data">
                        <%-- Giả sử đối tượng user được truyền vào có tên là "user" --%>
                        <c:set var="user" value="${requestScope.user}" />

                        <%-- Card 1: Thông tin khởi tạo --%>
                        <div class="profile-card">
                            <div class="card-body-split">
                                <div class="avatar-section">
                                    <img src="${empty user.avatarUrl ? 'https://placehold.co/170x170' : user.avatarUrl}" alt="Ảnh đại diện" id="avatarPreview">
                                    <input type="file" id="avatarUpload" name="avatar" hidden accept="image/*">
                                    <button type="button" class="btn btn-secondary" id="btnChooseAvatar">Thay đổi ảnh</button>
                                </div>
                                <div class="info-section">
                                    <h2>Thông tin khởi tạo</h2>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label>Mã nhân viên</label>
                                            <input type="text" value="${user.employeeCode}" disabled>
                                            <input type="hidden" name="employeeCode" value="${user.employeeCode}">
                                        </div>
                                    </div>
                                    <%-- SỬA LẠI PHẦN TÊN --%>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label for="lastName">Họ</label>
                                            <input type="text" id="lastName" name="lastName" value="${user.lastName}">
                                        </div>
                                        <div class="form-group">
                                            <label for="middleName">Tên đệm</label>
                                            <input type="text" id="middleName" name="middleName" value="${user.middleName}">
                                        </div>
                                        <div class="form-group">
                                            <label for="firstName">Tên</label>
                                            <input type="text" id="firstName" name="firstName" value="${user.firstName}">
                                        </div>
                                    </div>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label for="phoneNumber">Số điện thoại</label>
                                            <input type="tel" id="phoneNumber" name="phoneNumber" value="${user.phoneNumber}">
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <input type="hidden" name="id" value="${user.id}" />
                        <%-- Card 2: Thông tin công việc --%>
                        <div class="profile-card">
                            <div class="card-body">
                                <h2>Thông tin công việc</h2>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="department">Phòng làm việc</label>
                                        <select id="department" name="departmentId">
                                            <c:if test="${empty requestScope.departments}">
                                                <option value="">Không có phòng ban</option>
                                            </c:if>
                                            <c:forEach var="dept" items="${requestScope.departments}">
                                                <option value="${dept.id}" ${user.departmentId == dept.id ? 'selected' : ''}>${dept.name}</option>
                                            </c:forEach>
                                        </select>

                                    </div>
                                    <div class="form-group">
                                        <label for="position">Chức vụ</label>
                                        <input type="text" id="position" name="position" value="${user.positionName}">
                                    </div>
                                </div>
                                <div class="form-group full-width">
                                    <label for="notes">Ghi chú</label>
                                    <textarea id="notes" name="notes" rows="3">${user.notes}</textarea>
                                </div>
                            </div>
                        </div>

                        <%-- Card 3: Thông tin cá nhân --%>
                        <div class="profile-card">
                            <div class="card-body">
                                <h2>Thông tin cá nhân</h2>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="identityCardNumber">Số CMND/CCCD</label>
                                        <input type="text" id="identityCardNumber" name="identityCardNumber" value="${user.identityCardNumber}">
                                    </div>
                                    <div class="form-group">
                                        <label for="dateOfBirth">Ngày sinh</label>
                                        <input type="date" id="dateOfBirth" name="dateOfBirth" value="${user.dateOfBirth}">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label>Giới tính</label>
                                    <div class="radio-group">
                                        <label><input type="radio" name="gender" value="male" ${user.gender == 'male' ? 'checked' : ''}> Nam</label>
                                        <label><input type="radio" name="gender" value="female" ${user.gender == 'female' ? 'checked' : ''}> Nữ</label>
                                        <label><input type="radio" name="gender" value="other" ${user.gender == 'other' ? 'checked' : ''}> Khác</label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <%-- Card 4: Thông tin liên hệ --%>
                        <div class="profile-card">
                            <div class="card-body">
                                <h2>Thông tin liên hệ</h2>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="email">Email</label>
                                        <input type="email" id="email" name="email" value="${user.email}">
                                    </div>
                                    <div class="form-group">
                                        <label for="address">Địa chỉ</label>
                                        <input type="text" id="address" name="address" value="${user.streetAddress}">
                                    </div>

                                </div>
                                <div class="info-grid" style="margin-top: 1rem; grid-template-columns: repeat(3, 1fr);">
                                    <div class="form-group">
                                        <label for="province">Tỉnh/Thành phố (*)</label>
                                        <select id="province" name="province" class="form-control" required>
                                            <option value="" disabled ${empty user.provinceId ? 'selected' : ''}>-- Chọn Tỉnh/Thành --</option>
                                            <c:forEach var="p" items="${provinces}">
                                                <option value="${p.id}" ${user.provinceId == p.id ? 'selected' : ''}>${p.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="district">Quận/Huyện (*)</label>
                                        <select id="district" name="district" class="form-control" required disabled>
                                            <option value="" disabled selected>-- Chọn Quận/Huyện --</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="ward">Phường/Xã (*)</label>
                                        <select id="ward" name="ward" class="form-control" required disabled>
                                            <option value="" disabled selected>-- Chọn Phường/Xã --</option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <div class="form-actions">
                                <c:if test="${empty userID}">
                                    <a href="viewProfile?id=${user.id}" class="btn btn-secondary" role="button">Hủy</a>  
                                </c:if>
                                <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                            </div>
                    </form>
                </div>
            </main>
        </div>

        <script>
            feather.replace();
            // Khai báo global variables từ JSP
            window.userProvinceId = '${user.provinceId}' || '';
            window.userDistrictId = '${user.districtId}' || '';
            window.userWardId = '${user.wardId}' || '';
            window.BASE_URL = '${BASE_URL}';
        </script>
        <script src="http<s://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="js/mainMenu.js"></script>
        <script src="js/editProfile.js"></script>

    </body>
</html>