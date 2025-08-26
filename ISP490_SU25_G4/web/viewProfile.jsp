<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="currentPage" value="viewProfile" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thông tin cá nhân</title>
        <script src="https://unpkg.com/feather-icons"></script>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png" type="image/png">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/profile.css">
    </head>
    <body>

        <div class="app-container">
            <jsp:include page="mainMenu.jsp"/>

            <main class="main-content">
                <jsp:include page="header.jsp">
                    <jsp:param name="pageTitle" value="Thông tin cá nhân"/>
                </jsp:include>

                <div class="profile-form-container">
                    <c:if test="${not empty profile}">

                        <div class="profile-card avatar-card">
                            <c:choose>
                                <c:when test="${not empty profile.avatarUrl and profile.avatarUrl ne ''}">
                                    <img src="${pageContext.request.contextPath}/${profile.avatarUrl}" alt="Ảnh đại diện">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/image/default-avatar.png" alt="Ảnh đại diện mặc định">
                                </c:otherwise>
                            </c:choose>
                            <div>
                                <h1 class="profile-name">${profile.lastName} ${profile.middleName} ${profile.firstName}</h1>
                                <p class="profile-role">${profile.roleName}</p>
                            </div>
                        </div>

                        <div class="profile-card">
                            <h2>Thông tin cơ bản & Công việc</h2>
                            <div class="card-body">
                                <div class="info-grid">
                                    <div class="info-item">
                                        <label class="info-label">Mã nhân viên</label>
                                        <span class="info-data">${profile.employeeCode}</span>
                                    </div>
                                    <div class="info-item">
                                        <label class="info-label">Email</label>
                                        <span class="info-data">${profile.email}</span>
                                    </div>
                                    <div class="info-item">
                                        <label class="info-label">Số điện thoại</label>
                                        <span class="info-data">${not empty profile.phoneNumber ? profile.phoneNumber : 'Chưa cập nhật'}</span>
                                    </div>
                                    <div class="info-item">
                                        <label class="info-label">Phòng ban</label>
                                        <span class="info-data">${not empty profile.departmentName ? profile.departmentName : 'Chưa cập nhật'}</span>
                                    </div>

                                    <%-- Thêm trường Vị trí theo yêu cầu --%>
                                    <div class="info-item">
                                        <label class="info-label">Vị trí</label>
                                        <span class="info-data">${not empty profile.positionName ? profile.positionName : 'Chưa cập nhật'}</span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="profile-card">
                            <h2>Thông tin cá nhân & Địa chỉ</h2>
                            <div class="card-body">
                                <div class="info-grid">
                                    <div class="info-item">
                                        <label class="info-label">Số CMND/CCCD</label>
                                        <span class="info-data">${not empty profile.identityCardNumber ? profile.identityCardNumber : 'Chưa cập nhật'}</span>
                                    </div>
                                    <div class="info-item">
                                        <label class="info-label">Ngày sinh</label>
                                        <span class="info-data">
                                            <c:if test="${not empty profile.dateOfBirth}">
                                                <fmt:formatDate value="${profile.getDateOfBirthAsDate()}" pattern="dd/MM/yyyy"/>
                                            </c:if>
                                            <c:if test="${empty profile.dateOfBirth}">Chưa cập nhật</c:if>
                                            </span>
                                        </div>
                                        <div class="info-item">
                                            <label class="info-label">Giới tính</label>
                                            <span class="info-data">
                                            <c:choose>
                                                <c:when test="${profile.gender == 'male'}">Nam</c:when>
                                                <c:when test="${profile.gender == 'female'}">Nữ</c:when>
                                                <c:otherwise>Khác</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </div>
                                    <div class="info-item full-width">
                                        <label class="info-label">Địa chỉ</label>
                                        <%-- Sửa lại để ghép các thành phần địa chỉ --%>
                                        <span class="info-data">
                                            <c:if test="${not empty profile.streetAddress}">
                                                ${profile.streetAddress}, ${profile.wardName}, ${profile.districtName}, ${profile.provinceName}
                                            </c:if>
                                            <c:if test="${empty profile.streetAddress}">
                                                Chưa cập nhật
                                            </c:if>
                                        </span>
                                    </div>
                                    <div class="info-item full-width">
                                        <label class="info-label">Ghi chú</label>
                                        <span class="info-data">${not empty profile.notes ? profile.notes : 'Không có'}</span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="form-actions">
                            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary" role="button">Quay lại</a>
                            <a href="${pageContext.request.contextPath}/profile?action=edit" class="btn btn-primary" role="button">Sửa thông tin</a>
                        </div>
                    </c:if>

                    <c:if test="${empty profile}">
                        <p style="text-align: center; margin-top: 2rem;">Không thể tải thông tin người dùng.</p>
                    </c:if>
                </div>
            </main>
        </div>

        <script>
            feather.replace();
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>