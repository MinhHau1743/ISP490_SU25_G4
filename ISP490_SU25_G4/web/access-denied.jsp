<%-- 
    Document   : access-denied
    Created on : Jul 13, 2025, 4:44:04 PM
    Author     : ducanh
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Lỗi Truy Cập</title>
        <style>
            body {
                font-family: sans-serif;
                text-align: center;
                padding-top: 50px;
            }
            h1 {
                color: #d9534f;
            }
        </style>
    </head>
    <body>
        <h1>Lỗi 403: Truy cập bị từ chối</h1>
        <p>Bạn không có quyền truy cập vào chức năng này.</p>
        <a href="${pageContext.request.contextPath}/dashboard.jsp">Quay về Trang chủ</a>
    </body>
</html>
