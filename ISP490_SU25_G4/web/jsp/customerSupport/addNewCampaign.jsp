<%--
    Document   : addNewCampaign
    Created on : Jul 27, 2025, 5:00:00 PM
    Author     : minhh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- Import function tag library để sử dụng hàm split --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%-- Đặt trang hiện tại là 'campaigns' hoặc tương tự nếu có, hoặc để trống --%>
<c:set var="currentPage" value="campaigns" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thêm chiến dịch mới - DPCRM</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/listPlan.css"> <%-- Tái sử dụng một số kiểu --%>

        <style>
            /* Kiểu tùy chỉnh cho form Thêm chiến dịch mới */
            .modal-overlay {
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.5);
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 1000;
                opacity: 1; /* Luôn hiển thị cho ví dụ này, hoặc đặt 0 và dùng JS để bật/tắt */
                visibility: visible; /* Luôn hiển thị cho ví dụ này, hoặc đặt hidden và dùng JS để bật/tắt */
            }

            .modal-content {
                background: #fff;
                padding: 30px;
                border-radius: 8px;
                width: 100%;
                max-width: 600px; /* Điều chỉnh khi cần */
                box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
                position: relative;
                display: flex;
                flex-direction: column;
                max-height: 90vh; /* Cho phép cuộn nếu nội dung dài */
                overflow-y: auto;
            }

            .modal-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                border-bottom: 1px solid #eee;
                padding-bottom: 15px;
                margin-bottom: 20px;
            }

            .modal-header h2 {
                margin: 0;
                font-size: 1.5em;
                color: #333;
            }

            .modal-close-btn {
                background: none;
                border: none;
                font-size: 1.8em;
                cursor: pointer;
                color: #999;
                padding: 0;
                line-height: 1;
            }
            .modal-close-btn:hover {
                color: #555;
            }

            .form-group {
                margin-bottom: 20px;
            }

            .form-group label {
                display: block;
                font-weight: 600;
                margin-bottom: 8px;
                color: #333;
            }

            .form-group label .required-star {
                color: #dc3545; /* Màu đỏ cho trường bắt buộc */
                margin-left: 4px;
            }

            .form-group input[type="text"],
            .form-group input[type="number"],
            .form-group input[type="date"], /* Thêm kiểu cho input date */
            .form-group select,
            .form-group textarea {
                width: 100%;
                padding: 12px;
                border: 1px solid #ddd;
                border-radius: 6px;
                font-size: 1em;
                box-sizing: border-box; /* Bao gồm padding vào chiều rộng */
                transition: border-color 0.2s;
            }

            .form-group input[type="text"]:focus,
            .form-group input[type="number"]:focus,
            .form-group input[type="date"]:focus, /* Thêm kiểu cho input date */
            .form-group select:focus,
            .form-group textarea:focus {
                border-color: #007bff;
                outline: none;
            }

            .form-group textarea {
                resize: vertical; /* Cho phép thay đổi kích thước theo chiều dọc */
                min-height: 80px;
            }

            .form-row {
                display: flex;
                gap: 20px;
                margin-bottom: 20px;
            }

            .form-row .form-group {
                flex: 1;
                margin-bottom: 0; /* Ghi đè margin mặc định */
            }

            /* Thẻ đối tượng mục tiêu */
            .target-audience-tags {
                display: flex;
                flex-wrap: wrap;
                gap: 8px;
                padding: 8px;
                border: 1px solid #ddd;
                border-radius: 6px;
                min-height: 44px; /* Khớp chiều cao input */
                align-items: center;
            }
            .target-audience-tags .tag {
                background-color: #e2f0ff;
                color: #007bff;
                padding: 6px 10px;
                border-radius: 5px;
                display: flex;
                align-items: center;
                font-size: 0.9em;
            }
            .target-audience-tags .tag .tag-close {
                background: none;
                border: none;
                color: #007bff;
                margin-left: 8px;
                cursor: pointer;
                font-size: 1.1em;
                padding: 0;
                line-height: 1;
            }
            .target-audience-tags .tag .tag-close:hover {
                color: #0056b3;
            }
            .target-audience-input {
                flex-grow: 1;
                border: none;
                outline: none;
                padding: 0; /* Xóa padding input mặc định */
            }

            /* Tải file (Đính kèm) */
            .file-upload-area {
                border: 2px dashed #e0e0e0;
                border-radius: 8px;
                padding: 30px 20px;
                text-align: center;
                color: #999;
                font-size: 0.95em;
                cursor: pointer;
                transition: border-color 0.3s, background-color 0.3s;
            }
            .file-upload-area:hover, .file-upload-area.highlight { /* Thêm .highlight */
                border-color: #007bff;
                background-color: #f8faff;
            }
            .file-upload-area i {
                font-size: 2.5em;
                margin-bottom: 10px;
                display: block;
                color: #bbb;
            }
            .file-upload-area .browse-link {
                color: #007bff;
                text-decoration: none;
                font-weight: 500;
            }
            .file-upload-area .browse-link:hover {
                text-decoration: underline;
            }
            .file-upload-area input[type="file"] {
                display: none; /* Ẩn input file mặc định */
            }
            .max-size-info {
                font-size: 0.85em;
                color: #888;
                margin-top: 5px;
            }

            .modal-footer {
                display: flex;
                justify-content: flex-end;
                padding-top: 20px;
                border-top: 1px solid #eee;
                margin-top: 30px;
            }

            .btn-save-campaign {
                background-color: #007bff;
                color: #fff;
                padding: 12px 25px;
                border: none;
                border-radius: 6px;
                font-size: 1.1em;
                cursor: pointer;
                transition: background-color 0.2s;
            }
            .btn-save-campaign:hover {
                background-color: #0056b3;
            }

            /* Kiểu cho thông báo lỗi */
            .error-message {
                color: #dc3545;
                background-color: #f8d7da;
                border: 1px solid #f5c6cb;
                padding: 10px 15px;
                border-radius: 5px;
                margin-bottom: 20px;
            }
        </style>

    </head>
    <body>
        <div class="app-container">
            <%-- Bao gồm Menu chính --%>
            <jsp:include page="/mainMenu.jsp"/>

            <div class="content-area">
                <main class="main-content">
                    <%-- Đây là nơi bạn có thể thêm các phần nội dung khác của trang nếu đây không phải là một modal overlay --%>
                    <%-- Hiện tại, tôi đặt nó trong một overlay để mô phỏng giống hình ảnh --%>

                    <div class="modal-overlay">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h2>Thêm chiến dịch mới</h2>
                                <button type="button" class="modal-close-btn" onclick="window.location.href = '${pageContext.request.contextPath}/list-campaign';">
                                    <i data-feather="x"></i>
                                </button>
                            </div>

                            <%-- Hiển thị thông báo lỗi nếu có --%>
                            <c:if test="${not empty errorMessage}">
                                <div class="error-message">
                                    ${errorMessage}
                                </div>
                            </c:if>

                            <form action="${pageContext.request.contextPath}/add-campaign" method="post" enctype="multipart/form-data">
                                <div class="form-group">
                                    <label for="campaignName">Tên <span class="required-star">*</span></label>
                                    <input type="text" id="campaignName" name="campaignName" required placeholder="Nhập tên chủ đề chiến dịch..." value="${param.campaignName}">
                                </div>

                                <div class="form-group">
                                    <label for="createdBy">Người tạo <span class="required-star">*</span></label>
                                    <%-- Đã thay đổi từ input text sang select --%>
                                    <select id="createdBy" name="createdBy" required>
                                        <option value="">Chọn người tạo</option>
                                        <c:forEach var="user" items="${userList}">
                                            <option value="${user.id}" ${param.createdBy == user.id ? 'selected' : ''}>
                                                ${user.lastName} ${user.middleName} ${user.firstName} (${user.employeeCode})
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="startDate">Ngày bắt đầu <span class="required-star">*</span></label>
                                        <input type="date" id="startDate" name="startDate" required value="${param.startDate}">
                                    </div>
                                    <div class="form-group">
                                        <label for="endDate">Ngày kết thúc <span class="required-star">*</span></label>
                                        <input type="date" id="endDate" name="endDate" required value="${param.endDate}">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label for="campaignType">Loại Chiến dịch</label>
                                    <select id="campaignType" name="campaignType">
                                        <option value="">Chọn</option>
                                        <option value="marketing" ${param.campaignType == 'marketing' ? 'selected' : ''}>Tri ân và ưu đãi khách hàng</option>
                                        <option value="public_relations" ${param.campaignType == 'public_relations' ? 'selected' : ''}>Thu hút khách hàng tiềm năng</option>
                                        <option value="sales" ${param.campaignType == 'sales' ? 'selected' : ''}>Ra mắt sản phẩm mới</option>
                                        <option value="event" ${param.campaignType == 'event' ? 'selected' : ''}>Sự kiện công ty</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="period">Giai đoạn</label>
                                    <select id="period" name="period">
                                        <option value="">Chọn</option>
                                        <option value="daily" ${param.period == 'daily' ? 'selected' : ''}>Hàng ngày</option>
                                        <option value="weekly" ${param.period == 'weekly' ? 'selected' : ''}>Hàng tuần</option>
                                        <option value="monthly" ${param.period == 'monthly' ? 'selected' : ''}>Hàng tháng</option>
                                        <option value="quarterly" ${param.period == 'quarterly' ? 'selected' : ''}>Hàng quý</option>
                                        <option value="yearly" ${param.period == 'yearly' ? 'selected' : ''}>Hàng năm</option>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label for="targetAudience">Đối tượng mục tiêu</label>
                                    <div class="target-audience-tags" id="targetAudienceContainer">
                                        <%-- Duyệt và hiển thị lại các tag nếu có lỗi --%>
                                        <c:if test="${not empty param.targetAudience}">
                                            <c:set var="audienceTags" value="${fn:split(param.targetAudience, ',')}" />
                                            <c:forEach var="tag" items="${audienceTags}">
                                                <span class="tag">${tag} <button type="button" class="tag-close"><i data-feather="x"></i></button></span>
                                            </c:forEach>
                                        </c:if>
                                        <input type="text" class="target-audience-input" placeholder="Thêm đối tượng..." onkeydown="handleTagInput(event)">
                                        <input type="hidden" name="targetAudience" id="targetAudienceHidden" value="${param.targetAudience}">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label for="description">Mô tả <span class="required-star">*</span></label>
                                    <textarea id="description" name="description" required>${param.description}</textarea>
                                </div>

                                <div class="form-group">
                                    <label>Đính kèm</label>
                                    <div class="file-upload-area" id="fileDropArea">
                                        <i data-feather="folder"></i>
                                        Kéo và thả tệp của bạn tại đây hoặc <a href="#" class="browse-link" id="browseFilesLink">duyệt qua</a>
                                        <input type="file" id="attachmentInput" name="attachment" multiple> <%-- bỏ required nếu không bắt buộc --%>
                                    </div>
                                    <p class="max-size-info">Kích thước tối đa: 50 MB</p>
                                </div>

                                <div class="modal-footer">
                                    <button type="submit" class="btn-save-campaign">Lưu</button>
                                </div>
                            </form>
                        </div>
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

                // JS cơ bản cho thẻ Đối tượng mục tiêu (chỉ để minh họa)
                const targetAudienceContainer = document.getElementById('targetAudienceContainer');
                const targetAudienceInput = targetAudienceContainer.querySelector('.target-audience-input');
                const targetAudienceHidden = document.getElementById('targetAudienceHidden');

                function updateHiddenTags() {
                    // Lọc bỏ các phần tử tag đã bị xóa bởi người dùng (nếu có)
                    // .replace(/\s*x\s*$/, '') loại bỏ ' x' ở cuối chuỗi tag (từ nút đóng)
                    const tags = Array.from(targetAudienceContainer.querySelectorAll('.tag')).map(tagElement => tagElement.textContent.replace(/\s*x\s*$/, '').trim());
                    targetAudienceHidden.value = tags.join(',');
                }

                // Cập nhật ban đầu (đảm bảo giá trị từ param được chuyển vào input hidden)
                // Gọi updateHiddenTags() sau khi trang được tải để đảm bảo các tag từ ${param.targetAudience} được phản ánh.
                updateHiddenTags();

                // Xử lý thêm thẻ khi nhấn phím Enter
                window.handleTagInput = function (event) {
                    if (event.key === 'Enter' && targetAudienceInput.value.trim() !== '') {
                        event.preventDefault(); // Ngăn chặn gửi form
                        const newTagText = targetAudienceInput.value.trim();
                        const newTag = document.createElement('span');
                        newTag.className = 'tag';
                        newTag.innerHTML = `${newTagText} <button type="button" class="tag-close"><i data-feather="x"></i></button>`;

                        // Chèn thẻ mới vào trước trường input
                        targetAudienceContainer.insertBefore(newTag, targetAudienceInput);
                        targetAudienceInput.value = ''; // Xóa input
                        feather.replace({width: '1em', height: '1em'}); // Hiển thị lại icon feather cho nút mới
                        updateHiddenTags(); // Cập nhật lại giá trị input hidden
                    }
                };

                // Xử lý xóa thẻ
                targetAudienceContainer.addEventListener('click', function (event) {
                    if (event.target.closest('.tag-close')) {
                        event.target.closest('.tag').remove();
                        updateHiddenTags(); // Cập nhật lại giá trị input hidden
                    }
                });

                // JS cơ bản cho kéo & thả file (chỉ để minh họa)
                const fileDropArea = document.getElementById('fileDropArea');
                const attachmentInput = document.getElementById('attachmentInput');
                const browseFilesLink = document.getElementById('browseFilesLink');

                // Ngăn chặn các hành vi kéo mặc định
                ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
                    fileDropArea.addEventListener(eventName, preventDefaults, false);
                    document.body.addEventListener(eventName, preventDefaults, false); // Ngăn chặn kéo thả toàn cục
                });

                function preventDefaults(e) {
                    e.preventDefault();
                    e.stopPropagation();
                }

                // Đánh dấu vùng kéo khi có vật phẩm được kéo qua
                ['dragenter', 'dragover'].forEach(eventName => {
                    fileDropArea.addEventListener(eventName, highlight, false);
                });

                ['dragleave', 'drop'].forEach(eventName => {
                    fileDropArea.addEventListener(eventName, unhighlight, false);
                });

                function highlight(e) {
                    fileDropArea.classList.add('highlight');
                }

                function unhighlight(e) {
                    fileDropArea.classList.remove('highlight');
                }

                // Xử lý tệp được kéo vào
                fileDropArea.addEventListener('drop', handleDrop, false);

                function handleDrop(e) {
                    const dt = e.dataTransfer;
                    const files = dt.files;
                    attachmentInput.files = files; // Gán các tệp được kéo vào input
                    // Bạn thường sẽ hiển thị tên tệp tại đây hoặc kích hoạt tải lên
                    alert('Các tệp đã chọn: ' + Array.from(files).map(f => f.name).join(', '));
                }

                // Xử lý nhấp vào liên kết "duyệt qua"
                browseFilesLink.addEventListener('click', function (e) {
                    e.preventDefault();
                    attachmentInput.click(); // Kích hoạt nhấp vào input file ẩn
                });

                // Xử lý các tệp được chọn qua duyệt
                attachmentInput.addEventListener('change', function (e) {
                    const files = e.target.files;
                    // Bạn thường sẽ hiển thị tên tệp tại đây hoặc kích hoạt tải lên
                    alert('Các tệp đã chọn: ' + Array.from(files).map(f => f.name).join(', '));
                });
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>