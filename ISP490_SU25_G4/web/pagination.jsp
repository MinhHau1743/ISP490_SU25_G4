<%--
    Document   : pagination
    Created on : Jun 6, 2025
    Author     : minhnhn
    Version    : 6.1 (3-page sliding window)
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<c:set var="extraParams" value="${param.queryString}" />

<c:if test="${totalPages > 1}">
    <%-- === LOGIC TÍNH TOÁN 3 SỐ TRANG HIỂN THỊ === --%>
    <%-- 1. Đặt trang bắt đầu là trang hiện tại trừ 1 --%>
    <c:set var="begin" value="${currentPage - 1}" />

    <%-- 2. Xử lý trường hợp ở cuối danh sách --%>
    <c:if test="${totalPages > 3 && currentPage >= totalPages - 1}">
        <c:set var="begin" value="${totalPages - 2}" />
    </c:if>

    <%-- 3. Đảm bảo trang bắt đầu không nhỏ hơn 1 --%>
    <c:if test="${begin < 1}">
        <c:set var="begin" value="1" />
    </c:if>

    <%-- 4. Đặt trang kết thúc là trang bắt đầu + 2 --%>
    <c:set var="end" value="${begin + 2}" />

    <%-- 5. Đảm bảo trang kết thúc không lớn hơn tổng số trang --%>
    <c:if test="${end > totalPages}">
        <c:set var="end" value="${totalPages}" />
    </c:if>
    <%-- === KẾT THÚC LOGIC TÍNH TOÁN === --%>

    <div class="table-footer">
        <div class="pagination">
            <a href="?page=1&size=${pageSize}${extraParams}"
               class="page-link ${currentPage == 1 ? 'disabled' : ''}">
                &lt;&lt;
            </a>

            <a href="?page=${currentPage - 1}&size=${pageSize}${extraParams}"
               class="page-link ${currentPage == 1 ? 'disabled' : ''}">
                &lt;
            </a>

            <c:forEach begin="${begin}" end="${end}" var="i">
                <a href="?page=${i}&size=${pageSize}${extraParams}"
                   class="page-link ${i == currentPage ? 'active' : ''}">
                    ${i}
                </a>
            </c:forEach>

            <a href="?page=${currentPage + 1}&size=${pageSize}${extraParams}"
               class="page-link ${currentPage == totalPages ? 'disabled' : ''}">
                &gt;
            </a>

            <a href="?page=${totalPages}&size=${pageSize}${extraParams}"
               class="page-link ${currentPage == totalPages ? 'disabled' : ''}">
                &gt;&gt;
            </a>
        </div>
    </div>
</c:if>