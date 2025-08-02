<%--
    Document   : viewCampaignDetails
    Created on : Jul 30, 2025
    Author     : minhh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="currentPage" value="campaigns" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết: ${campaign.name} - DPCRM</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script src="https://unpkg.com/feather-icons"></script>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/listCampaign.css">

        <style>
            /* THIẾT KẾ MỚI DỰA TRÊN HÌNH ẢNH CUNG CẤP */
            .main-content {
                padding: 20px 30px;
                background-color: #f4f7f9; /* Thêm màu nền cho khu vực nội dung chính */
            }
            .detail-container {
                background: var(--white);
                padding: 30px 40px;
                border-radius: 12px;
                width: 100%;
                max-width: 900px; /* Làm cho form rộng ra */
                margin: 20px auto; /* Căn form ra giữa */
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
                border: 1px solid #e9ecef;
            }

            .detail-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 30px;
                padding-bottom: 20px;
                border-bottom: 1px solid #e9ecef;
            }

            .detail-header h1 {
                margin: 0;
                font-size: 1.8em;
                color: #212529;
                font-weight: 600;
            }

            .btn-back {
                padding: 8px 16px;
                border-radius: 6px;
                font-size: 0.9em;
                font-weight: 500;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 6px;
                background-color: #e9ecef;
                color: #495057;
                border: 1px solid #dee2e6;
                transition: background-color 0.2s, border-color 0.2s;
            }
            .btn-back:hover {
                background-color: #dee2e6;
                border-color: #ced4da;
            }
            
            .detail-body {
                display: flex;
                flex-direction: column;
                gap: 20px; /* Khoảng cách giữa các dòng thông tin */
            }

            .detail-group {
                margin: 0;
            }

            .detail-group label {
                display: block;
                font-weight: 500;
                margin-bottom: 8px;
                color: #6c757d;
                font-size: 0.8em;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }

            .detail-value {
                padding: 12px 15px;
                background-color: #f8f9fa;
                border: 1px solid #e9ecef;
                border-radius: 6px;
                font-size: 1em;
                font-weight: 500;
                color: #212529;
                word-wrap: break-word;
                min-height: 45px;
                display: flex;
                align-items: center;
            }
            
            .detail-row {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 20px;
            }
            
            .error-message {
                text-align: center;
                padding: 15px;
                background-color: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
                border-radius: 6px;
            }
            
            .text-muted {
                color: var(--text-secondary);
                font-weight: 400;
            }
            
            /* Ghi đè style cho status badge để không có box nền xám */
            .status-group .detail-value {
                background: none;
                border: none;
                padding: 0;
            }
            
            .attachment-link {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                text-decoration: none;
                font-weight: 500;
                color: #0056b3;
            }
            .attachment-link:hover {
                text-decoration: underline;
            }
            
            .detail-footer {
                display: flex;
                justify-content: flex-end;
                margin-top: 30px;
                padding-top: 20px;
                border-top: 1px solid #e9ecef;
            }
            .btn-edit {
                padding: 8px 16px;
                border-radius: 6px;
                font-size: 0.9em;
                font-weight: 500;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 6px;
                background-color: #007bff;
                color: white;
                border: 1px solid #007bff;
                transition: background-color 0.2s, border-color 0.2s;
            }
             .btn-edit:hover {
                background-color: #0056b3;
                border-color: #0056b3;
            }
        </style>

    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>

            <div class="content-area">
                <main class="main-content">
                    <div class="detail-container">
                        <c:if test="${empty campaign}">
                            <div class="error-message">Không tìm thấy chiến dịch.</div>
                        </c:if>

                        <c:if test="${not empty campaign}">
                            <div class="detail-header">
                                <h1>Chi tiết: <c:out value="${campaign.name}"/></h1>
                                <a href="${pageContext.request.contextPath}/list-campaign" class="btn-back">
                                    <i data-feather="arrow-left" style="width: 16px; height: 16px;"></i> Quay lại
                                </a>
                            </div>
                            
                            <div class="detail-body">
                                <div class="detail-group">
                                    <label>Tên Chiến dịch</label>
                                    <div class="detail-value"><c:out value="${campaign.name}"/></div>
                                </div>

                                <div class="detail-group">
                                    <label>Người tạo</label>
                                    <div class="detail-value">
                                        <c:if test="${not empty campaign.user}">
                                            <c:out value="${campaign.user.lastName} ${campaign.user.middleName} ${campaign.user.firstName}"/>
                                            <span class="text-muted" style="margin-left: 5px;">(<c:out value="${campaign.user.employeeCode}"/>)</span>
                                        </c:if>
                                        <c:if test="${empty campaign.user}">
                                            <span class="text-muted">Không xác định (ID: <c:out value="${campaign.createdBy}"/>)</span>
                                        </c:if>
                                    </div>
                                </div>

                                <div class="detail-row">
                                    <div class="detail-group">
                                        <label>Ngày bắt đầu</label>
                                        <div class="detail-value">
                                            <fmt:formatDate value="${campaign.startDate}" pattern="dd/MM/yyyy" />
                                        </div>
                                    </div>
                                    <div class="detail-group">
                                        <label>Ngày kết thúc</label>
                                        <div class="detail-value">
                                            <fmt:formatDate value="${campaign.endDate}" pattern="dd/MM/yyyy" />
                                        </div>
                                    </div>
                                </div>

                                <div class="detail-group status-group">
                                    <label>Trạng thái</label>
                                    <div class="detail-value">
                                        <span class="status-badge 
                                              <c:choose>
                                                  <c:when test="${campaign.status == 'draft'}">status-draft</c:when>
                                                  <c:when test="${campaign.status == 'active'}">status-active</c:when>
                                                  <c:when test="${campaign.status == 'ended'}">status-ended</c:when>
                                                  <c:when test="${campaign.status == 'canceled'}">status-canceled</c:when>
                                              </c:choose>">
                                            <c:choose>
                                                <c:when test="${campaign.status == 'draft'}"><i data-feather="edit-3"></i> Nháp</c:when>
                                                <c:when test="${campaign.status == 'active'}"><i data-feather="play-circle"></i> Đang hoạt động</c:when>
                                                <c:when test="${campaign.status == 'ended'}"><i data-feather="check-circle"></i> Đã kết thúc</c:when>
                                                <c:when test="${campaign.status == 'canceled'}"><i data-feather="x-circle"></i> Đã hủy</c:when>
                                            </c:choose>
                                        </span>
                                    </div>
                                </div>

                                <div class="detail-row">
                                    <div class="detail-group">
                                        <label>Ngày tạo</label>
                                        <div class="detail-value">
                                            <fmt:formatDate value="${campaign.createdAt}" pattern="HH:mm dd/MM/yyyy" />
                                        </div>
                                    </div>
                                    <div class="detail-group">
                                        <label>Ngày cập nhật</label>
                                        <div class="detail-value">
                                            <fmt:formatDate value="${campaign.updatedAt}" pattern="HH:mm dd/MM/yyyy" />
                                        </div>
                                    </div>
                                </div>

                                <div class="detail-group">
                                    <label>Mô tả</label>
                                    <div class="detail-value">
                                        <c:out value="${not empty campaign.description ? campaign.description : 'Không có mô tả.'}"/>
                                    </div>
                                </div>

                                <div class="detail-group">
                                    <label>Tệp đính kèm</label>
                                    <div class="detail-value">
                                        <c:choose>
                                            <c:when test="${not empty campaign.attachmentFileName}">
                                                <a href="${pageContext.request.contextPath}/download-attachment?file=${campaign.attachmentFileName}" class="attachment-link" target="_blank">
                                                    <i data-feather="paperclip"></i>
                                                    <span><c:out value="${campaign.attachmentFileName}"/></span>
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Không có tệp đính kèm.</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                
                                <div class="detail-footer">
                                      <a href="${pageContext.request.contextPath}/edit-campaign?id=${campaign.campaignId}" class="btn-edit">
                                          <i data-feather="edit" style="width: 16px; height: 16px;"></i> Sửa
                                      </a>
                                </div>

                            </div>
                        </c:if>
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
            });
        </script>
        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
    </body>
</html>