<%--
    Document   : viewEmployee
    Created on : Jun 14, 2025
    Author     : NGUYEN MINH
    
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="currentPage" value="listEmployee" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thông tin chi tiết nhân viên</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewEmployee.css">

        <script src="https://unpkg.com/feather-icons"></script>
    </head>

    <style>
        .info-text {
            display: flex;
            flex-direction: column;
            margin-bottom: 16px;
            font-family: Arial, sans-serif;
        }

        .info-text label {
            font-weight: bold;
            margin-bottom: 6px;
            color: #333;
        }

        .info-text input[type="date"] {
            padding: 8px 12px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
            font-family: inherit;
            color: #333;
            background-color: #fff;
            width: 100%;
            max-width: 300px; /* optional */
            box-sizing: border-box;
        }
    </style>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>

            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">Thông tin chi tiết nhân viên</div>
                </header>

                <section class="content-body">
                    <c:if test="${not empty employee}">
                        <div class="view-employee-page">

                            <div class="avatar-panel">
                                <div class="avatar-display-box">
                                    <c:url var="avatarUrl" value="/images/default-avatar.png" />
                                    <c:if test="${not empty employee.avatarUrl}">
                                        <c:set var="avatarUrl" value="/${employee.avatarUrl}" />
                                    </c:if>
                                    <img src="${pageContext.request.contextPath}${avatarUrl}" alt="Ảnh đại diện">
                                </div>
                                <h2 class="employee-name-title">${employee.lastName} ${employee.middleName} ${employee.firstName}</h2>
                                <p class="employee-code-title">Mã NV: ${employee.employeeCode}</p>
                            </div>

                            <div class="info-panel">
                                <%-- === CARD 1: THÔNG TIN CƠ BẢN (ĐÃ SẮP XẾP LẠI) === --%>
                                <div class="info-card">
                                    <h3 class="info-card-title">Thông tin cơ bản</h3>
                                    <div class="info-grid">
                                        <div class="info-item">
                                            <i data-feather="calendar"></i>
                                            <div class="info-text">
                                                <label>Ngày sinh</label>
                                                <input type="date" name="dateOfBirth" value="${employee.dateOfBirth}" />
                                            </div>
                                        </div>
                                        <div class="info-item">
                                            <i data-feather="users"></i>
                                            <div class="info-text">
                                                <label>Giới tính</label>
                                                <c:set var="genderDisplay">
                                                    <c:choose>
                                                        <c:when test="${employee.gender == 'male'}">Nam</c:when>
                                                        <c:when test="${employee.gender == 'female'}">Nữ</c:when>
                                                        <c:when test="${employee.gender == 'other'}">Khác</c:when>
                                                        <c:otherwise>Chưa cập nhật</c:otherwise>
                                                    </c:choose>
                                                </c:set>
                                                <span>${genderDisplay}</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <%-- === CARD 2: THÔNG TIN ĐỊNH DANH & LIÊN HỆ (ĐÃ SẮP XẾP LẠI) === --%>
                                <div class="info-card">
                                    <h3 class="info-card-title">Thông tin định danh & Liên hệ</h3>
                                    <div class="info-grid">
                                        <div class="info-item">
                                            <i data-feather="mail"></i>
                                            <div class="info-text">
                                                <label>Email</label>
                                                <span>${not empty employee.email ? employee.email : 'Chưa cập nhật'}</span>
                                            </div>
                                        </div>
                                        <div class="info-item">
                                            <i data-feather="phone"></i>
                                            <div class="info-text">
                                                <label>Số điện thoại</label>
                                                <span>${not empty employee.phoneNumber ? employee.phoneNumber : 'Chưa cập nhật'}</span>
                                            </div>
                                        </div>
                                        <div class="info-item full-width">
                                            <i data-feather="credit-card"></i>
                                            <div class="info-text">
                                                <label>Số CMND/CCCD</label>
                                                <span>${not empty employee.identityCardNumber ? employee.identityCardNumber : 'Chưa cập nhật'}</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <%-- === CARD 3: THÔNG TIN CÔNG VIỆC (ĐÃ SẮP XẾP LẠI) === --%>
                                <%-- === CARD 3: THÔNG TIN CÔNG VIỆC (ĐÃ SẮP XẾP LẠI) === --%>
                                <div class="info-card">
                                    <h3 class="info-card-title">Thông tin công việc</h3>
                                    <div class="info-grid">
                                        <div class="info-item">
                                            <i data-feather="briefcase"></i>
                                            <div class="info-text">
                                                <label>Phòng làm việc</label>
                                                <span>${not empty employee.departmentName ? employee.departmentName : 'Chưa cập nhật'}</span>
                                            </div>
                                        </div>
                                        <div class="info-item">
                                            <i data-feather="award"></i>
                                            <div class="info-text">
                                                <label>Chức vụ</label>
                                                <span>${not empty employee.positionName ? employee.positionName : 'Chưa cập nhật'}</span>
                                            </div>
                                        </div>
                                        <div class="info-item">
                                            <i data-feather="clock"></i>
                                            <div class="info-text">
                                                <label>Thời gian tạo</label>
                                                <span><fmt:formatDate value="${employee.createdAt}" pattern="HH:mm dd/MM/yyyy" /></span>
                                            </div>
                                        </div>
                                        <%-- **THAY ĐỔI Ở ĐÂY**: Đã chuyển Ghi chú vào trong lưới --%>
                                        <div class="info-item">
                                            <i data-feather="file-text"></i>
                                            <div class="info-text">
                                                <label>Ghi chú</label>
                                                <p>${not empty employee.notes ? employee.notes : 'Không có ghi chú.'}</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>


                    <c:if test="${empty employee}">
                        <p>Không tìm thấy thông tin nhân viên.</p>
                    </c:if>
                </section>

                <footer class="page-actions-footer">
                    <button type="button" class="btn btn-secondary" onclick="window.location.href = '${pageContext.request.contextPath}/employee?action=list'">Quay lại danh sách</button>
                    <a href="${pageContext.request.contextPath}/employee?action=edit&id=${employee.id}" class="btn btn-primary" role="button">Sửa thông tin</a>
                </footer>
            </main>
        </div>
        <script>
            feather.replace();
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>