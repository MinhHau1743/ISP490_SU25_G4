<%-- 
    Document   : editProfile.jsp
    Created on : Jun 5, 2024, 9:27:00 PM
    Author     : minhnhn (đã được AI cập nhật)
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="BASE_URL" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chỉnh sửa thông tin cá nhân</title>
        <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png" type="image/png">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="${BASE_URL}/css/style.css">
        <link rel="stylesheet" href="${BASE_URL}/css/header.css">
        <link rel="stylesheet" href="${BASE_URL}/css/mainMenu.css">
        <link rel="stylesheet" href="${BASE_URL}/css/editprofile.css">
    </head>
    <body>

        <div class="app-container">
            <jsp:include page="mainMenu.jsp"/>

            <main class="main-content">
                <jsp:include page="header.jsp">
                    <jsp:param name="pageTitle" value="Chỉnh sửa thông tin"/>
                </jsp:include>

                <div class="profile-form-container">
                    <c:if test="${not empty requestScope.error}">
                        <div class="alert alert-danger">${requestScope.error}</div>
                    </c:if>

                    <form id="editProfileForm" action="${BASE_URL}/profile" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="action" value="update" />

                        <c:set var="user" value="${requestScope.user}" />
                        <input type="hidden" name="id" value="${user.id}" />

                        <div class="profile-card">
                            <h2>Thông tin cơ bản</h2>
                            <div class="card-body-split">
                                <div class="avatar-section">
                                    <c:choose>
                                        <c:when test="${not empty user.avatarUrl and user.avatarUrl ne ''}">
                                            <img src="${BASE_URL}/${user.avatarUrl}" alt="Ảnh đại diện" id="avatarPreview">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${BASE_URL}/image/default-avatar.png" alt="Ảnh đại diện mặc định" id="avatarPreview">
                                        </c:otherwise>
                                    </c:choose>
                                    <input type="file" id="avatarUpload" name="avatar" hidden accept="image/*">
                                    <button type="button" class="btn btn-secondary" id="btnChooseAvatar">Thay đổi ảnh</button>
                                </div>
                                <div class="info-section">
                                    <div class="form-group">
                                        <label>Mã nhân viên</label>
                                        <input type="text" value="${user.employeeCode}" disabled>
                                    </div>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label for="lastName">Họ</label>
                                            <input type="text" id="lastName" name="lastName" value="${user.lastName}" required>
                                        </div>
                                        <div class="form-group">
                                            <label for="middleName">Tên đệm</label>
                                            <input type="text" id="middleName" name="middleName" value="${user.middleName}">
                                        </div>
                                        <div class="form-group">
                                            <label for="firstName">Tên</label>
                                            <input type="text" id="firstName" name="firstName" value="${user.firstName}" required>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label for="phoneNumber">Số điện thoại</label>
                                        <input type="tel" id="phoneNumber" name="phoneNumber" value="${user.phoneNumber}">
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="profile-card">
                            <h2>Thông tin công việc</h2>
                            <div class="card-body">
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="departmentId">Phòng ban</label>
                                        <select id="departmentId" name="departmentId">
                                            <c:forEach var="dept" items="${requestScope.departments}">
                                                <option value="${dept.id}" ${user.departmentId == dept.id ? 'selected' : ''}>${dept.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="positionId">Vị trí</label>
                                        <select id="positionId" name="positionId">
                                            <c:forEach var="pos" items="${requestScope.positions}">
                                                <option value="${pos.id}" ${user.positionId == pos.id ? 'selected' : ''}>${pos.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <input type="hidden" name="roleId" value="${user.roleId}" />
                                </div>
                                <div class="form-group full-width">
                                    <label for="notes">Ghi chú</label>
                                    <textarea id="notes" name="notes" rows="3">${user.notes}</textarea>
                                </div>
                            </div>
                        </div>

                        <div class="profile-card">
                            <h2>Thông tin cá nhân</h2>
                            <div class="card-body">
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

                        <div class="profile-card">
                            <h2>Thông tin liên hệ</h2>
                            <div class="card-body">
                                <div class="form-group">
                                    <label for="email">Email</label>
                                    <input type="email" id="email" name="email" value="${user.email}" disabled>
                                </div>
                                <div class="form-group">
                                    <label for="streetAddress">Số nhà, tên đường</label>
                                    <input type="text" id="streetAddress" name="streetAddress" value="${user.streetAddress}">
                                </div>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="provinceId">Tỉnh/Thành phố (*)</label>
                                        <select id="provinceId" name="provinceId" required>
                                            <option value="" ${empty user.provinceId ? 'selected' : ''}>-- Chọn Tỉnh/Thành --</option>
                                            <c:forEach var="p" items="${provinces}">
                                                <option value="${p.id}" ${user.provinceId == p.id ? 'selected' : ''}>${p.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="districtId">Quận/Huyện (*)</label>
                                        <select id="districtId" name="districtId" required>
                                            <option value="">-- Chọn Quận/Huyện --</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="wardId">Phường/Xã (*)</label>
                                        <select id="wardId" name="wardId" required>
                                            <option value="">-- Chọn Phường/Xã --</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="form-actions">
                            <a href="${BASE_URL}/profile" class="btn btn-secondary" role="button">Hủy</a>  
                            <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                        </div>
                    </form>
                </div>
            </main>
        </div>

        <script>
            feather.replace();

            // Truyền các giá trị cần thiết từ JSP sang JavaScript
            // File editProfile.js sẽ sử dụng các biến này
            window.BASE_URL = '${BASE_URL}';
            window.userDistrictId = '${user.districtId}';
            window.userWardId = '${user.wardId}';
        </script>
        <script src="${BASE_URL}/js/mainMenu.js"></script>
        <script src="${BASE_URL}/js/editProfile.js"></script>
    </body>
</html>