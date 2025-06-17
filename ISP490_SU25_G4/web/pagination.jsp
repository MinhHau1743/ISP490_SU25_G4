<%-- 
    Document   : pagination
    Created on : Jun 6, 2025, 2:48:22 PM
    Author     : minhnhn
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="table-footer">
    <div class="pagination">
        <div class="pagination-icon"><i data-feather="list"></i></div>
        <span>Hiển thị trang</span>
        <span class="page-number active">${currentPage}</span>
        <span>trên ${totalPages} trang</span>

        <!-- Nút lùi -->
        <c:choose>
            <c:when test="${currentPage > 1}">
                <a href="?page=${currentPage - 1}&size=${pageSize}" class="page-nav">
                    <i data-feather="chevron-left"></i>
                </a>
            </c:when>
            <c:otherwise>
                <span class="page-nav disabled"><i data-feather="chevron-left"></i></span>
            </c:otherwise>
        </c:choose>

        <!-- Nút tới -->
        <c:choose>
            <c:when test="${currentPage < totalPages}">
                <a href="?page=${currentPage + 1}&size=${pageSize}" class="page-nav">
                    <i data-feather="chevron-right"></i>
                </a>
            </c:when>
            <c:otherwise>
                <span class="page-nav disabled"><i data-feather="chevron-right"></i></span>
            </c:otherwise>
        </c:choose>
    </div>
</div>

