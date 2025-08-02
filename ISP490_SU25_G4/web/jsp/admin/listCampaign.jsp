<%--
    Document   : listplan
    Created on : Jul 21, 2025, 11:32:12 PM
    Author     : minhh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Đặt trang hiện tại là 'support' để làm nổi bật mục "Hỗ trợ Kỹ thuật" trong menu --%>
<c:set var="currentPage" value="support" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Danh sách kế hoạch - DPCRM</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">

        <%-- Thêm file CSS cho bảng, nên tạo file mới css/table.css --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/listCampaign.css">

        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">

        <style>
            /* Thêm CSS cơ bản cho cột hành động và các icon */
            .actions-cell {
                white-space: nowrap; /* Ngăn các icon bị xuống dòng */
            }
            .actions-cell .icon-btn {
                background: none;
                border: none;
                cursor: pointer;
                padding: 4px;
                margin: 0 2px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                color: #555; /* Màu icon mặc định */
            }
            .actions-cell .icon-btn:hover {
                color: #007bff; /* Màu hover */
            }

            /* Kiểu mới cho trạng thái chiến dịch */
            .status-badge {
                display: inline-flex;
                align-items: center;
                gap: 5px;
                padding: 5px 10px;
                border-radius: 6px;
                font-size: 0.85em;
                font-weight: 500;
                white-space: nowrap; /* Đảm bảo không xuống dòng */
            }

            .status-draft { /* Nháp */
                background-color: #e0f2fe; /* blue-100 */
                color: #0369a1; /* blue-700 */
            }

            .status-active { /* Đang hoạt động */
                background-color: #dcfce7; /* green-100 */
                color: #166534; /* green-700 */
            }

            .status-pending { /* Chờ xử lý */
                background-color: #fffbeb; /* yellow-100 */
                color: #b45309; /* yellow-700 */
            }

            .status-ended { /* Đã kết thúc */
                background-color: #f3f4f6; /* gray-100 */
                color: #4b5563; /* gray-700 */
            }

            .status-canceled { /* Đã hủy */
                background-color: #fee2e2; /* red-100 */
                color: #b91c1c; /* red-700 */
            }

            /* CSS cho dropdown trạng thái (đã tối ưu để tránh lỗi tràn chữ) */
            .status-select {
                padding: 5px 25px 5px 8px; /* Tăng padding-right để nhường chỗ cho mũi tên */
                border-radius: 6px;
                border: 1px solid #ccc;
                font-size: 0.85em;
                font-weight: 500;
                background-color: #fff;
                -webkit-appearance: none; /* Loại bỏ mũi tên mặc định trên Chrome/Safari */
                -moz-appearance: none;    /* Loại bỏ mũi tên mặc định trên Firefox */
                appearance: none;         /* Loại bỏ mũi tên mặc định */
                background-image: url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%23000000%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-6.5%200-12.3%203.2-16.1%208.1-3.9%204.9-4.8%2011.9-2.4%2017.7l139.3%20162c5.9%206.8%2015.6%206.8%2021.5%200l139.3-162c2.4-5.8%201.5-12.8-2.4-17.7z%22%2F%3E%3C%2Fsvg%3E');
                background-repeat: no-repeat;
                background-position: right 8px center; /* Căn giữa dọc tốt hơn */
                background-size: 10px auto;
                cursor: pointer;
                min-width: 140px; /* Đảm bảo đủ rộng cho các trạng thái, ví dụ "Đang hoạt động" */
                width: auto; /* Cho phép chiều rộng tự động điều chỉnh theo nội dung */
                max-width: 100%; /* Đảm bảo không tràn ra khỏi cột của bảng */
                box-sizing: border-box; /* Bao gồm padding và border trong tổng kích thước */
                white-space: nowrap; /* Giữ văn bản trên một dòng */
                /* Loại bỏ overflow: hidden và text-overflow: ellipsis để văn bản không bị cắt */
            }

            /* Thêm màu nền cho option dựa trên giá trị */
            .status-select option[value="draft"] {
                background-color: #e0f2fe;
            }
            .status-select option[value="active"] {
                background-color: #dcfce7;
            }
            .status-select option[value="pending"] {
                background-color: #fffbeb;
            }
            .status-select option[value="ended"] {
                background-color: #f3f4f6;
            }
            .status-select option[value="canceled"] {
                background-color: #fee2e2;
            }

            .status-select.draft {
                background-color: #e0f2fe; /* blue-100 */
                color: #0369a1; /* blue-700 */
            }
            .status-select.active {
                background-color: #dcfce7; /* green-100 */
                color: #166534; /* green-700 */
            }
            .status-select.pending {
                background-color: #fffbeb; /* yellow-100 */
                color: #b45309; /* yellow-700 */
            }
            .status-select.ended {
                background-color: #f3f4f6; /* gray-100 */
                color: #4b5563; /* gray-700 */
            }
            .status-select.canceled {
                background-color: #fee2e2; /* red-100 */
                color: #b91c1c; /* red-700 */
            }

            /* Original success message for initial load (hidden as SweetAlert2 handles it) */
            .success-message {
                display: none; /* Ẩn div này vì SweetAlert2 sẽ xử lý thông báo */
            }

            /* Thêm CSS cho bảng và cột để đảm bảo hiển thị đúng */
            .data-table {
                width: 100%; /* Đảm bảo bảng sử dụng toàn bộ chiều rộng có sẵn */
                table-layout: auto; /* Cho phép các cột tự điều chỉnh chiều rộng dựa trên nội dung */
            }

            /* Đảm bảo cột "Trạng thái" có đủ không gian */
            .data-table th:nth-child(6),
            .data-table td:nth-child(6) {
                min-width: 160px; /* Đảm bảo cột có đủ không gian cho dropdown và padding của nó */
            }

            /* Đảm bảo nội dung trong các ô không bị tràn ngoài ý muốn */
            .data-table td {
                word-wrap: break-word; /* Cho phép từ dài bị ngắt nếu cần để tránh tràn ô */
            }

            /* Thêm CSS tùy chỉnh cho phân trang nếu cần, nhưng thường Bootstrap sẽ đủ */
            /* Ví dụ nếu bạn muốn override một số style mặc định của Bootstrap hoặc thêm khoảng cách */
            .pagination {
                margin-top: 20px; /* Khoảng cách trên phân trang */
            }
        </style>

    </head>
    <body>
        <div class="app-container">
            <%-- Include Main Menu --%>
            <jsp:include page="/mainMenu.jsp"/>

            <div class="content-area"> 

                <main class="main-content">

                    <%-- DIV này đã được ẩn và sẽ không hiển thị, SweetAlert2 sẽ xử lý thông báo --%>
                    <%-- <c:if test="${param.success eq 'true'}">
                                        <div class="success-message">
                                            Thêm chiến dịch mới thành công!
                                        </div>
                                    </c:if> --%>

                    <%-- Lưới thống kê --%>
                    <section class="stats-grid">
                        <div class="stat-card">
                            <div class="icon-container icon-yellow"><i data-feather="mail"></i></div>
                            <div class="info">
                                <div class="title">Chiến dịch</div>
                                <div class="value">474</div>
                            </div>

                        </div>

                        <div class="stat-card">
                            <div class="icon-container icon-red"><i data-feather="eye"></i></div>
                            <div class="info">
                                <div class="title">Đã mở</div>
                                <div class="value">650</div>
                            </div>

                        </div>
                        <div class="stat-card">
                            <div class="icon-container icon-green"><i data-feather="check-circle"></i></div>
                            <div class="info">
                                <div class="title">Hoàn thành</div>
                                <div class="value">650</div>
                            </div>

                        </div>
                    </section>

                    <%-- Tabs điều hướng --%>
                    <nav class="campaign-tabs">
                        <ul>
                            <li><a href="#">Chiến dịch hoạt động hiện tại</a></li>

                        </ul>
                    </nav>

                    <%-- Thanh công cụ của bảng --%>
                    <section class="campaign-toolbar">
                        <div class="toolbar-top">
                            <%-- ĐÃ SỬA: Thêm form tìm kiếm --%>
                            <form action="${pageContext.request.contextPath}/list-campaign" method="GET" class="search-bar">
                                <i data-feather="search"></i>
                                <input type="text" name="search" placeholder="Tìm kiếm chiến dịch..." value="${searchTerm}">
                                <%-- Thêm nút submit ẩn hoặc visible tùy ý. Nếu ẩn, nhấn Enter trong ô input sẽ submit form. --%>
                                <button type="submit" style="display: none;"></button> 
                            </form>
                            <%-- Đã sửa đường dẫn này để gọi Servlet AddCampaignServlet --%>
                            <a href="${pageContext.request.contextPath}/add-campaign" class="btn-add-campaign">
                                <i data-feather="plus"></i>
                                Thêm chiến dịch mới
                            </a>
                        </div>

                    </section>

                    <%-- Bảng dữ liệu --%>
                    <section class="data-table-container">
                        <table class="data-table">
                            <thead>
                                <tr>        
                                    <th>ID</th>
                                    <th><input type="checkbox"> Tên</th>  
                                    <th>Người tạo</th>                                   
                                    <th>Ngày bắt đầu</th>
                                    <th>Ngày kết thúc</th>
                                    <th>Trạng thái</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="c" items="${campaigns}">
                                    <tr>
                                        <td>${c.campaignId}</td>
                                        <td><input type="checkbox"> <strong>${c.name}</strong></td>
                                        <td>
                                            ${c.user.lastName}  ${c.user.middleName} ${c.user.firstName}
                                            <br>
                                            <small class="text-muted">(${c.user.employeeCode})</small>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${c.startDate}" pattern="dd/MM/yyyy" />
                                        </td>
                                        <td><fmt:formatDate value="${c.endDate}" pattern="dd/MM/yyyy" /></td>
                                        <td>
                                            <%-- Store the original status in a data attribute --%>
                                            <select class="status-select ${c.status}" data-original-status="${c.status}"
                                                     onchange="updateCampaignStatus(${c.campaignId}, this.value, this)">
                                                <option value="draft" <c:if test="${c.status == 'draft'}">selected</c:if>>Nháp</option>
                                                <option value="active" <c:if test="${c.status == 'active'}">selected</c:if>>Đang hoạt động</option>
                                                <option value="ended" <c:if test="${c.status == 'ended'}">selected</c:if>>Đã kết thúc</option>
                                                <option value="canceled" <c:if test="${c.status == 'canceled'}">selected</c:if>>Đã hủy</option>
                                                </select>
                                            </td>
                                            <td class="actions-cell">
                                                <a href="${pageContext.request.contextPath}/view-campaign-detail?id=${c.campaignId}" class="icon-btn" title="Xem">
                                                <i data-feather="eye"></i>
                                            </a>
                                            <%-- Đã sửa: Thay đổi button thành link đến trang chỉnh sửa --%>
                                            <a href="${pageContext.request.contextPath}/edit-campaign?id=${c.campaignId}" class="icon-btn" title="Sửa">
                                                <i data-feather="edit"></i>
                                            </a>
                                            <button class="icon-btn" title="Xóa"><i data-feather="trash-2"></i></button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </section>
                    <%-- ĐÃ SỬA: Cập nhật cách gọi pagination.jsp để truyền searchTerm --%>
                    <jsp:include page="/pagination.jsp">
                        <jsp:param name="searchTerm" value="${searchTerm}" />
                    </jsp:include>
                </main>

            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz" crossorigin="anonymous"></script>
        
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.all.min.js"></script> 

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace({
                    width: '1em',
                    height: '1em'
                });

                const urlParams = new URLSearchParams(window.location.search);

                // Xử lý thông báo thành công chung (từ thêm mới hoặc cập nhật)
                if (urlParams.get('success')) {
                    const successMessage = decodeURIComponent(urlParams.get('success')); // Giải mã thông báo
                    Swal.fire({
                        toast: true,
                        position: 'top-end',
                        icon: 'success',
                        title: successMessage, // Sử dụng thông báo đã giải mã ở đây
                        showConfirmButton: false,
                        timer: 3000,
                        timerProgressBar: true
                    });
                } else if (urlParams.get('error')) { // Xử lý thông báo lỗi từ các redirect
                    Swal.fire({
                        toast: true,
                        position: 'top-end',
                        icon: 'error',
                        title: decodeURIComponent(urlParams.get('error')),
                        showConfirmButton: false,
                        timer: 5000,
                        timerProgressBar: true
                    });
                }
            });

            // Hàm JavaScript để cập nhật trạng thái chiến dịch
            function updateCampaignStatus(campaignId, newStatus, element) {
                const originalStatus = element.dataset.originalStatus; // Lấy trạng thái gốc từ data attribute 

                // Sử dụng SweetAlert2 cho hộp thoại xác nhận 
                Swal.fire({
                    title: 'Xác nhận thay đổi trạng thái?',
                    text: 'Bạn có chắc chắn muốn chuyển trạng thái chiến dịch này sang "' + getStatusDisplayName(newStatus) + '" không?',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: 'Có, thay đổi!',
                    cancelButtonText: 'Hủy bỏ' // Thêm văn bản cho nút hủy 
                }).then((result) => {
                    if (result.isConfirmed) { // Nếu người dùng nhấn 'Có, thay đổi!' 
                        element.disabled = true; // Vô hiệu hóa dropdown trong khi gửi yêu cầu 

                        fetch('${pageContext.request.contextPath}/update-campaign-status', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded',
                            },
                            body: 'id=' + campaignId + '&status=' + newStatus
                        })
                                .then(response => response.text()) // Đọc phản hồi dưới dạng văn bản 
                                .then(data => {
                                    if (data.trim() === 'Success') {
                                        Swal.fire({// Hiển thị thông báo thành công bằng SweetAlert2 toast 
                                            toast: true,
                                            position: 'top-end',
                                            icon: 'success',
                                            title: 'Trạng thái chiến dịch đã được cập nhật thành "' + getStatusDisplayName(newStatus) + '".',
                                            showConfirmButton: false,
                                            timer: 3000,
                                            timerProgressBar: true
                                        });
                                        // Cập nhật class để đổi màu cho dropdown 
                                        element.className = 'status-select ' + newStatus;
                                        // Cập nhật data-original-status attribute 
                                        element.dataset.originalStatus = newStatus;
                                        element.disabled = false; // Tái kích hoạt dropdown 
                                    } else {
                                        Swal.fire({// Hiển thị thông báo lỗi bằng SweetAlert2 toast 
                                            toast: true,
                                            position: 'top-end',
                                            icon: 'error',
                                            title: 'Lỗi: ' + data,
                                            showConfirmButton: false,
                                            timer: 3000,
                                            timerProgressBar: true
                                        });
                                        element.disabled = false; // Tái kích hoạt dropdown nếu có lỗi mạng 
                                        // Khôi phục về giá trị cũ nếu cập nhật thất bại 
                                        element.value = originalStatus;
                                        element.className = 'status-select ' + originalStatus; // Khôi phục class màu 
                                    }
                                })
                                .catch((error) => {
                                    console.error('Error:', error);
                                    Swal.fire({// Thông báo lỗi mạng bằng SweetAlert2 toast 
                                        toast: true,
                                        position: 'top-end',
                                        icon: 'error',
                                        title: 'Có lỗi xảy ra khi gửi yêu cầu. Vui lòng thử lại.',
                                        showConfirmButton: false,
                                        timer: 3000,
                                        timerProgressBar: true
                                    });
                                    element.disabled = false; // Tái kích hoạt dropdown nếu có lỗi mạng 
                                    element.value = originalStatus; // Khôi phục về giá trị cũ 
                                    element.className = 'status-select ' + originalStatus; // Khôi phục class màu 
                                });
                    } else {
                        // Nếu người dùng nhấn 'Hủy bỏ', reset dropdown về giá trị ban đầu 
                        element.value = originalStatus;
                    }
                });
            }

            // Hàm để lấy tên hiển thị của trạng thái 
            function getStatusDisplayName(status) {
                switch (status) {
                    case 'draft':
                        return 'Nháp';
                    case 'active':
                        return 'Đang hoạt động';
                    case 'ended':
                        return 'Đã kết thúc';
                    case 'canceled':
                        return 'Đã hủy';
                    default:
                        return status;
                }
            }
        </script> 
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script> 
    </body> 
</html>