<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tất cả Thông báo</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/mainMenu.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
        <script src="https://unpkg.com/feather-icons"></script>
        <style>
            .notification-page-content {
                padding: 2rem;
                background-color: #f9fafb;
            }
            .notification-list-full {
                background-color: #fff;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.05);
                overflow: hidden;
            }
            .notification-item {
                display: flex;
                align-items: center;
                padding: 1rem 1.5rem;
                border-bottom: 1px solid #f0f0f0;
                text-decoration: none;
                color: inherit;
                transition: background-color 0.2s ease;
            }
            .notification-item:hover {
                background-color: #f9f9f9;
            }
            .notification-item:last-child {
                border-bottom: none;
            }
            .notification-icon {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                margin-right: 1.5rem;
            }
            .notification-icon i {
                width: 20px;
                height: 20px;
            }
            .notification-content {
                flex-grow: 1;
            }
            .notification-title {
                font-weight: 600;
                margin: 0 0 4px 0;
                font-size: 1rem;
                color: #333;
            }
            .notification-details {
                margin: 0;
                font-size: 0.9rem;
                color: #666;
            }
            .notification-time {
                margin-top: 8px;
                font-size: 0.8rem;
                color: #999;
                text-align: right;
            }
             .notification-empty {
                padding: 3rem 1rem;
                text-align: center;
                color: #999;
            }
            .notification-empty i {
                width: 48px;
                height: 48px;
                margin-bottom: 1rem;
            }
        </style>
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="/mainMenu.jsp"/>
            <main class="main-content">
                <jsp:include page="/header.jsp">
                    <jsp:param name="pageTitle" value="Tất cả Thông báo"/>
                </jsp:include>
                
                <div class="notification-page-content">
                    <div class="notification-list-full">
                        <c:if test="${empty notificationList}">
                             <div class="notification-empty">
                                <i data-feather="bell-off"></i>
                                <p>Không có thông báo nào.</p>
                            </div>
                        </c:if>
                        
                        <c:forEach var="noti" items="${notificationList}">
                             <a href="${pageContext.request.contextPath}/${noti.linkUrl}" class="notification-item">
                                <%-- Logic chọn icon và màu sắc (tương tự header) --%>
                                <c:set var="iconBg" value="#e3f2fd"/><c:set var="iconColor" value="#2196f3"/><c:set var="iconName" value="info"/>
                                <c:if test="${noti.notificationType == 'ENTERPRISE'}"><c:set var="iconBg" value="#e8eaf6"/><c:set var="iconColor" value="#3f51b5"/><c:set var="iconName" value="briefcase"/></c:if>
                                <c:if test="${noti.notificationType == 'CONTRACT'}"><c:set var="iconBg" value="#e0f7fa"/><c:set var="iconColor" value="#00796b"/><c:set var="iconName" value="file-text"/></c:if>
                                <c:if test="${noti.notificationType == 'TECH_REQUEST'}"><c:set var="iconBg" value="#fff3e0"/><c:set var="iconColor" value="#ff9800"/><c:set var="iconName" value="tool"/></c:if>
                                <c:if test="${noti.notificationType == 'CAMPAIGN'}"><c:set var="iconBg" value="#ede7f6"/><c:set var="iconColor" value="#5e35b1"/><c:set var="iconName" value="volume-2"/></c:if>
                                <c:if test="${noti.notificationType == 'FEEDBACK'}"><c:set var="iconBg" value="#e8f5e9"/><c:set var="iconColor" value="#4caf50"/><c:set var="iconName" value="message-square"/></c:if>
                                
                                <div class="notification-icon" style="background-color: ${iconBg}; color: ${iconColor};">
                                    <i data-feather="${iconName}"></i>
                                </div>
                                <div class="notification-content">
                                    <p class="notification-title">${noti.title}</p>
                                    <p class="notification-details">${noti.message}</p>
                                </div>
                                 <div class="notification-time">${noti.relativeTime}</div>
                            </a>
                        </c:forEach>
                    </div>
                    
                    <%-- Include phần phân trang --%>
                    <c:if test="${totalPages > 1}">
                        <jsp:include page="/pagination.jsp">
                            <jsp:param name="currentPage" value="${currentPage}"/>
                            <jsp:param name="totalPages" value="${totalPages}"/>
                            <jsp:param name="baseUrl" value="notifications?action=list"/>
                        </jsp:include>
                    </c:if>
                </div>
            </main>
        </div>

        <script src="${pageContext.request.contextPath}/js/mainMenu.js"></script>
        <script>
            feather.replace();
        </script>
    </body>
</html>