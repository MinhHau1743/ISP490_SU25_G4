<%-- 
    Document   : dashboard
    Created on : Jun 6, 2025, 2:45:19 PM
    Author     : minhnhn
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="currentPage" value="dashboard" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>DPCRM</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="stylesheet" href="css/style.css">       
        <link rel="stylesheet" href="css/header.css">
        <link rel="stylesheet" href="css/mainMenu.css">
        <link rel="stylesheet" href="css/pagination.css">
    </head>
    <body>
        <div class="app-container">
            <jsp:include page="mainMenu.jsp"/>
            <main class="main-content">
                <header class="main-top-bar">
                    <div class="page-title">DPCRM</div>
                    <button class="notification-btn">
                        <i data-feather="bell"></i>
                        <span class="notification-badge"></span>
                    </button>
                </header>

                <section class="content-body">
                    <div class="table-toolbar">
                    </div>

                    <div class="empty-state">
                    </div>
                    <jsp:include page="pagination.jsp" />
                </section>
            </main>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function() {
                feather.replace();
            });
        </script>

        <script src="js/mainMenu.js"></script>
    </body>
</html>