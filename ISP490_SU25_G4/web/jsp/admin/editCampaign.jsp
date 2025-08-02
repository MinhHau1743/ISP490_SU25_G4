<%--
    Document   : editCampaign
    Created on : Jul 29, 2025 (or later), based on viewCampaignDetails
    Author     : minhh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="currentPage" value="campaigns" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chỉnh sửa Chiến dịch - DPCRM</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">


        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/listCampaign.css"> <%-- Reusing status styles --%>

        <style>
            /* Copy styles from viewCampaignDetails.jsp and modify for edit form */
            .detail-container {
                background: var(--white);
                padding: 20px 25px;
                border-radius: 10px;
                width: 100%;
                max-width: 850px;
                margin: 30px auto;
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
                display: flex;
                flex-direction: column;
            }

            .detail-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                border-bottom: 1px solid var(--border-color);
                padding-bottom: 15px;
                margin-bottom: 25px;
            }

            .detail-header h2 {
                margin: 0;
                font-size: 1.8em;
                color: var(--text-primary);
                font-weight: 700;
            }

            .detail-group {
                margin-bottom: 18px;
            }

            .detail-group label {
                display: block;
                font-weight: 600;
                margin-bottom: 6px;
                color: var(--text-secondary);
                font-size: 0.85em;
                text-transform: uppercase;
            }

            /* Styles for input fields in the edit form */
            .detail-input,
            .detail-textarea,
            .detail-select {
                width: 100%; /* Đã sửa: Chiếm 100% chiều rộng của parent, padding đã được tính bằng box-sizing */
                padding: 10px 15px;
                background-color: var(--background-color);
                border: 1px solid var(--border-color);
                border-radius: 6px;
                font-size: 1em;
                color: var(--text-primary);
                background-color: var(--white); /* Ensure background is white for inputs */
                box-sizing: border-box; /* Include padding and border in width */
            }

            .detail-input[type="date"] {
                width: fit-content; /* Allow date input to shrink */
            }
            
            /* Style cho input file */
            .detail-input[type="file"] {
                padding: 8px;
            }
            .detail-input[type="file"]::file-selector-button {
                margin-right: 10px;
                border: none;
                background: #007bff;
                padding: 8px 12px;
                border-radius: 4px;
                color: #fff;
                cursor: pointer;
                transition: background .2s ease-in-out;
            }
            .detail-input[type="file"]::file-selector-button:hover {
                background: #0056b3;
            }


            textarea.detail-textarea {
                min-height: 100px;
                resize: vertical;
            }

            .detail-row {
                display: flex;
                gap: 18px;
                margin-bottom: 18px;
            }

            .detail-row .detail-group {
                flex: 1;
                margin-bottom: 0;
            }

            .detail-footer {
                display: flex;
                justify-content: flex-end;
                padding-top: 20px;
                border-top: 1px solid var(--border-color);
                margin-top: 30px;
            }

            .btn-action {
                padding: 10px 20px;
                border-radius: 6px;
                font-size: 0.95em;
                gap: 6px;
                font-weight: 600;
                display: inline-flex; /* To align icon and text */
                align-items: center;
                justify-content: center;
                cursor: pointer;
                text-decoration: none; /* For anchor tags */
            }

            .btn-back {
                background-color: var(--text-secondary);
                color: var(--white);
                border: none;
                margin-right: 10px;
            }
            .btn-back:hover {
                background-color: #5a6268;
            }

            .btn-save {
                background-color: var(--primary-color);
                color: var(--white);
                border: none;
            }
            .btn-save:hover {
                background-color: #0056b3;
            }

            .error-message {
                color: var(--danger-color);
                background-color: #fee2e2;
                border: 1px solid var(--danger-color);
                padding: 12px 18px;
                border-radius: 6px;
                margin-bottom: 18px;
                text-align: center;
                font-weight: 500;
                font-size: 0.95em;
            }

            /* Styles for status badge (if you still want to display it as a badge) */
            .status-badge {
                display: inline-flex;
                align-items: center;
                gap: 5px;
                padding: 5px 10px;
                border-radius: 6px;
                font-size: 0.85em;
                font-weight: 500;
                white-space: nowrap;
            }
            .status-draft {
                background-color: #e0f2fe;
                color: #0369a1;
            }
            .status-active {
                background-color: #dcfce7;
                color: #166534;
            }
            .status-pending {
                background-color: #fffbeb;
                color: #b45309;
            }
            .status-ended {
                background-color: #f3f4f6;
                color: #4b5563;
            }
            .status-canceled {
                background-color: #fee2e2;
                color: #b91c1c;
            }

            /* Text muted for small details */
            .text-muted {
                color: var(--text-secondary);
                font-size: 0.8em;
            }

            /* CSS cho dropdown trạng thái trong edit form (giống listCampaign) */
            .detail-select {
                padding: 5px 25px 5px 8px; /* Tăng padding-right để nhường chỗ cho mũi tên */
                border-radius: 6px;
                border: 1px solid #ccc;
                font-size: 0.85em;
                font-weight: 500;
                background-color: #fff;
                -webkit-appearance: none;
                -moz-appearance: none;
                appearance: none;
                background-image: url('data:image/svg+xml;charset=US-ASCII,%3Csvg%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20width%3D%22292.4%22%20height%3D%22292.4%22%3E%3Cpath%20fill%3D%22%23000000%22%20d%3D%22M287%2069.4a17.6%2017.6%200%200%200-13-5.4H18.4c-6.5%200-12.3%203.2-16.1%208.1-3.9%204.9-4.8%2011.9-2.4%2017.7l139.3%20162c5.9%206.8%2015.6%206.8%2021.5%200l139.3-162c2.4-5.8%201.5-12.8-2.4-17.7z%22%2F%3E%3C%2Fsvg%3E');
                background-repeat: no-repeat;
                background-position: right 8px center;
                background-size: 10px auto;
                cursor: pointer;
                min-width: 140px;
                width: auto; /* Cho phép chiều rộng tự động điều chỉnh theo nội dung */
                max-width: 100%;
                box-sizing: border-box;
                white-space: nowrap;
            }
            /* Thêm màu nền cho option dựa trên giá trị */
            .detail-select option[value="draft"] {
                background-color: #e0f2fe;
            }
            .detail-select option[value="active"] {
                background-color: #dcfce7;
            }
            .detail-select option[value="ended"] {
                background-color: #f3f4f6;
            }
            .detail-select option[value="canceled"] {
                background-color: #fee2e2;
            }

            .detail-select.draft {
                background-color: #e0f2fe;
                color: #0369a1;
            }
            .detail-select.active {
                background-color: #dcfce7;
                color: #166534;
            }
            .detail-select.pending {
                background-color: #fffbeb;
                color: #b45309;
            }
            .detail-select.ended {
                background-color: #f3f4f6;
                color: #4b5563;
            }
            .detail-select.canceled {
                background-color: #fee2e2;
                color: #b91c1c;
            }

            /* Định nghĩa màu mới cho nút Save */
            :root {
                --primary-color: #007bff; /* Màu xanh mặc định của btn-save */
                --text-secondary: #6c757d; /* Màu mặc định của btn-back */
                --danger-color: #dc3545;
                --border-color: #e0e0e0;
                --background-color: #f8f9fa;
                --white: #ffffff;
                --text-primary: #333;

                /* THÊM MÀU XANH THEO YÊU CẦU */
                --custom-green-teal: #3b9c9c; /* Một ví dụ màu xanh teal */
            }

            .btn-save {
                background-color: var(--custom-green-teal); /* Sử dụng màu xanh teal */
                color: var(--white);
                border: none;
            }
            .btn-save:hover {
                background-color: #307e7e; /* Màu hover tối hơn một chút */
            }

            /* CSS for current attachment display */
            .current-attachment-info {
                background-color: #f0f8ff;
                border: 1px dashed #add8e6;
                padding: 10px 15px;
                border-radius: 6px;
                margin-top: 8px;
                margin-bottom: 12px;
                display: flex;
                align-items: center;
                gap: 10px;
                font-size: 0.95em;
            }
            .current-attachment-info a {
                font-weight: 600;
                color: #0056b3;
                text-decoration: none;
            }
            .current-attachment-info a:hover {
                text-decoration: underline;
            }

        </style>

    </head>
    <body>
        <div class="app-container">
            <%-- Include Main Menu --%>
            <jsp:include page="/mainMenu.jsp"/>

            <div class="content-area">
                <main class="main-content">
                    <div class="detail-container">
                        <div class="detail-header">
                            <h2>Chỉnh sửa: <c:out value="${campaign.name}" /></h2>
                            <a href="${pageContext.request.contextPath}/list-campaign" class="btn-action btn-back" title="Quay lại">
                                <i data-feather="arrow-left"></i> 
                            </a>
                        </div>

                        <%-- Display error message if campaign not found (e.g., direct access with invalid ID) --%>
                        <c:if test="${empty campaign}">
                            <div class="error-message">
                                Không tìm thấy chiến dịch để chỉnh sửa. Vui lòng kiểm tra lại ID.
                            </div>
                        </c:if>

                        <%-- Display form if campaign object exists --%>
                        <c:if test="${not empty campaign}">
                            <%-- ========================================================================= --%>
                            <%-- THAY ĐỔI 1: Thêm enctype="multipart/form-data" để cho phép upload file --%>
                            <%-- ========================================================================= --%>
                            <form action="${pageContext.request.contextPath}/update-campaign" method="POST" enctype="multipart/form-data">
                                <%-- Hidden field for campaign ID --%>
                                <input type="hidden" name="campaignId" value="${campaign.campaignId}">
                                <%-- Hidden field for createdBy (if not editable by user) --%>
                                <input type="hidden" name="createdBy" value="${campaign.createdBy}">

                                <div class="detail-group">
                                    <label for="campaignName">Tên Chiến dịch:</label>
                                    <input type="text" id="campaignName" name="name" class="detail-input" 
                                           value="<c:out value="${campaign.name}" />" required>
                                </div>

                                <div class="detail-group">
                                    <label>Người tạo:</label>
                                    <div class="detail-value">
                                        <c:if test="${not empty campaign.user}">
                                            ${campaign.user.lastName} ${campaign.user.middleName} ${campaign.user.firstName}
                                            (<small>${campaign.user.employeeCode}</small>)
                                        </c:if>
                                        <c:if test="${empty campaign.user}">
                                            <span class="text-muted">Không xác định (ID: ${campaign.createdBy})</span>
                                        </c:if>
                                    </div>
                                    <small class="text-muted">Người tạo không thể thay đổi.</small>
                                </div>

                                <div class="detail-row">
                                    <div class="detail-group">
                                        <label for="startDate">Ngày bắt đầu:</label>
                                        <input type="date" id="startDate" name="startDate" class="detail-input"
                                               value="<fmt:formatDate value="${campaign.startDate}" pattern="yyyy-MM-dd" />" required>
                                    </div>
                                    <div class="detail-group">
                                        <label for="endDate">Ngày kết thúc:</label>
                                        <input type="date" id="endDate" name="endDate" class="detail-input"
                                               value="<fmt:formatDate value="${campaign.endDate}" pattern="yyyy-MM-dd" />" required>
                                    </div>
                                </div>

                                <div class="detail-group">
                                    <label for="status">Trạng thái:</label>
                                    <select id="status" name="status" class="detail-select">
                                        <option value="draft" <c:if test="${campaign.status == 'draft'}">selected</c:if>>Nháp</option>
                                        <option value="active" <c:if test="${campaign.status == 'active'}">selected</c:if>>Đang hoạt động</option>
                                        <option value="ended" <c:if test="${campaign.status == 'ended'}">selected</c:if>>Đã kết thúc</option>
                                        <option value="canceled" <c:if test="${campaign.status == 'canceled'}">selected</c:if>>Đã hủy</option>
                                    </select>
                                </div>

                                    <div class="detail-group">
                                            <label>Ngày tạo:</label>
                                            <div class="detail-value">
                                    <fmt:formatDate value="${campaign.createdAt}" pattern="HH:mm dd/MM/yyyy" />
                                </div>
                                <small class="text-muted">Ngày tạo không thể thay đổi.</small>
                            </div>

                            <div class="detail-group">
                                <label>Ngày cập nhật:</label>
                                <div class="detail-value">
                                    <fmt:formatDate value="${campaign.updatedAt}" pattern="HH:mm dd/MM/yyyy" />
                                </div>
                                <small class="text-muted">Ngày cập nhật sẽ tự động thay đổi khi lưu.</small>
                            </div>

                            <div class="detail-group">
                                <label for="description">Mô tả:</label>
                                <textarea id="description" name="description" class="detail-textarea"><c:out value="${campaign.description}" /></textarea>
                            </div>

                            <%-- ========================================================================= --%>
                            <%-- THAY ĐỔI 2: Cập nhật phần Đính kèm để cho phép upload file             --%>
                            <%-- ========================================================================= --%>
                            <div class="detail-group">
                                <label for="attachmentFile">Tệp đính kèm:</label>

                                <%-- Hiển thị tệp hiện tại nếu có --%>
                                <c:choose>
                                    <c:when test="${not empty campaign.attachmentFileName}">
                                        <div class="current-attachment-info">
                                            <i data-feather="file-text"></i>
                                            <span>Tệp hiện tại: 
                                                <%-- Giả sử bạn có một servlet để tải file --%>
                                                <a href="${pageContext.request.contextPath}/download-attachment?file=${campaign.attachmentFileName}" target="_blank">
                                                    <c:out value="${campaign.attachmentFileName}"/>
                                                </a>
                                            </span>
                                        </div>
                                        <small class="text-muted">Tải lên tệp mới sẽ thay thế tệp hiện tại.</small>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted">Không có tệp đính kèm.</p>
                                    </c:otherwise>
                                </c:choose>
                                
                                <%-- Input để tải lên tệp mới --%>
                                <input type="file" id="attachmentFile" name="attachmentFile" class="detail-input" style="margin-top: 5px;">
                                <input type="hidden" name="currentAttachment" value="${campaign.attachmentFileName}">
                            </div>


                                <div class="detail-footer">
                                    <a href="${pageContext.request.contextPath}/view-campaign-detail?id=${campaign.campaignId}" class="btn-action btn-back">
                                        <i data-feather="x"></i> Hủy
                                    </a>
                                    <button type="submit" class="btn-action btn-save">
                                        <i data-feather="save"></i> Lưu 
                                    </button>
                                </div>
                            </form>
                        </c:if> <%-- Kết thúc c:if test not empty campaign --%>
                    </div>
                </main>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                feather.replace({
                    width: '1em',
                    height: '1em'
                });

                // Bạn có thể thêm logic hiển thị thông báo SweetAlert2 ở đây
                // Ví dụ: khi có tham số 'error' trên URL sau khi submit form không thành công
                const urlParams = new URLSearchParams(window.location.search);
                if (urlParams.get('error')) {
                    Swal.fire({
                        toast: true,
                        position: 'top-end',
                        icon: 'error',
                        title: decodeURIComponent(urlParams.get('error')), // Giải mã URL nếu có ký tự đặc biệt
                        showConfirmButton: false,
                        timer: 5000,
                        timerProgressBar: true
                    });
                } else if (urlParams.get('success')) { // Xử lý thông báo thành công
                    Swal.fire({
                        toast: true,
                        position: 'top-end',
                        icon: 'success',
                        title: decodeURIComponent(urlParams.get('success')),
                        showConfirmButton: false,
                        timer: 3000,
                        timerProgressBar: true
                    });
                }


                // Nếu muốn thay đổi màu của select khi tải trang (giống listCampaign.jsp)
                const statusSelect = document.getElementById('status');
                if (statusSelect) {
                    statusSelect.className = 'detail-select ' + statusSelect.value;
                    // Thêm sự kiện để cập nhật màu ngay lập tức khi người dùng thay đổi
                    statusSelect.addEventListener('change', function () {
                        this.className = 'detail-select ' + this.value;
                    });
                }
            });
        </script>
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.all.min.js"></script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>