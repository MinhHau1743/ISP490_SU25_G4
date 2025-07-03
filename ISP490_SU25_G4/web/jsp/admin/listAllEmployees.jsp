<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="currentPage" value="listEmployeeCustomer" />
<c:set var="isEmployeeSectionActive" value="true" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách nhân viên</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/employeeCard.css">

    <style>
        /* Reset and base styles */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f4f6f9;
        }

        /* Employee grid layout */
        .employee-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            padding: 20px;
            max-width: 1200px;
            margin: 0 auto;
        }

        /* Employee card styles */
        .employee-card {
            background-color: #ffffff;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            padding: 20px;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .employee-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
        }

        /* Card main info */
        .card-main {
            text-align: center;
        }

        .employee-name {
            font-size: 1.2rem;
            font-weight: 600;
            color: #333333;
            margin-bottom: 5px;
        }

        .employee-code {
            font-size: 0.875rem;
            color: #6c757d;
        }

        /* Card secondary info */
        .card-secondary-info {
            display: flex;
            flex-direction: column;
            gap: 8px;
            margin-top: 10px;
        }

        .info-row {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 0.95rem;
            color: #555555;
        }

        .info-row i {
            width: 20px;
            height: 20px;
            stroke-width: 2px;
            color: #007bff;
        }

        .position-badge {
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 0.85rem;
            background-color: #e9f4ff;
            color: #007bff;
            font-weight: 500;
        }

        /* Card actions */
        .card-actions {
            display: flex;
            justify-content: center;
            gap: 15px;
            margin-top: 10px;
        }

        .card-actions a {
            text-decoration: none;
            color: #555555;
            transition: color 0.3s ease;
        }

        .card-actions a:hover {
            color: #007bff;
        }

        .card-actions i {
            width: 20px;
            height: 20px;
            stroke-width: 2px;
        }

        /* Empty state message */
        .employee-grid p {
            grid-column: 1 / -1;
            text-align: center;
            font-size: 1.1rem;
            color: #6c757d;
            font-style: italic;
        }

        /* Responsive design */
        @media (max-width: 600px) {
            .employee-grid {
                grid-template-columns: 1fr;
                padding: 10px;
            }

            .employee-card {
                padding: 15px;
            }

            .employee-name {
                font-size: 1.1rem;
            }

            .employee-code {
                font-size: 0.85rem;
            }
        }
    </style>
</head>
<body>
    <div class="app-container">
        <jsp:include page="/menu.jsp" />
        <main class="main-content">
            <header class="main-top-bar">
                <div class="page-title">Danh sách Nhân viên</div>
                <button class="notification-btn">
                    <i data-feather="bell"></i>
                </button>
            </header>

            <section class="content-body">
                <div class="table-toolbar">
                    <div class="search-bar">
                        <i data-feather="search"></i>
                        <input type="text" placeholder="Tìm kiếm nhân viên...">
                    </div>
                      <a href="${pageContext.request.contextPath}/admin/employees/add" class="btn btn-primary">
                        <i data-feather="plus"></i> Thêm nhân viên
                    </a>
                </div>

                <div class="employee-grid">
                    <c:if test="${empty employeeList}">
                        <p>Không có nhân viên nào để hiển thị.</p>
                    </c:if>

                    <c:forEach var="user" items="${employeeList}">
                        <div class="employee-card">
                            <div class="card-main">
                                <h3 class="employee-name">${user.lastName} ${user.middleName} ${user.firstName}</h3>
                                <p class="employee-code">${user.employeeCode}</p>
                            </div>
                            <div class="card-secondary-info">
                                <div class="info-row">
                                    <i data-feather="shield"></i>
                                    <span class="info-label">
                                        ${empty user.roleName ? 'Chưa có vai trò' : user.roleName}
                                    </span>
                                </div>
                                <div class="info-row">
                                    <i data-feather="briefcase"></i>
                                    <span class="info-label">
                                        ${empty user.departmentName ? 'Chưa có phòng ban' : user.departmentName}
                                    </span>
                                </div>
                                <div class="info-row">
                                    <i data-feather="award"></i>
                                    <span class="position-badge">
                                        ${empty user.positionName ? 'Nhân viên' : user.positionName}
                                    </span>
                                </div>
                            </div>
                            <div class="card-actions">
                                <a href="${pageContext.request.contextPath}/admin/employees/view?id=${user.id}" title="Xem chi tiết">
                                    <i data-feather="eye"></i>
                                </a>
                                <a href="${pageContext.request.contextPath}/admin/employees/edit?id=${user.id}" title="Sửa">
                                    <i data-feather="edit-2"></i>
                                </a>
                                <a href="${pageContext.request.contextPath}/admin/employees/delete?id=${user.id}" 
                                   title="Xóa" 
                                   onclick="return confirm('Bạn chắc chắn muốn xóa nhân viên ${user.lastName} ${user.middleName} ${user.firstName}?');">
                                    <i data-feather="trash-2"></i>
                                </a>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <jsp:include page="/pagination.jsp" />
            </section>
        </main>
    </div>

    <script src="https://unpkg.com/feather-icons"></script>
    <script src="${pageContext.request.contextPath}/js/listEmployee.js"></script>
</body>
</html>