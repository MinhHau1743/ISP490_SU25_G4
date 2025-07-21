<%--
    Document   : pagination (Generic & Bootstrap 5)
    Version    : 8.0
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%-- Logic xây dựng extraParams giữ nguyên --%>
<c:set var="extraParams" value="" />
<c:forEach var="p" items="${param}">
    <c:if test="${p.key != 'page'}">
        <c:forEach var="value" items="${p.value}">
            <c:set var="extraParams" value="${extraParams}&${p.key}=${fn:escapeXml(value)}" />
        </c:forEach>
    </c:if>
</c:forEach>


<c:if test="${totalPages > 1}">
    <%-- Logic tính toán sliding window giữ nguyên --%>
    <c:set var="begin" value="${currentPage - 1}" />
    <c:if test="${totalPages > 3 && currentPage >= totalPages - 1}"><c:set var="begin" value="${totalPages - 2}" /></c:if>
    <c:if test="${begin < 1}"><c:set var="begin" value="1" /></c:if>
    <c:set var="end" value="${begin + 2}" />
    <c:if test="${end > totalPages}"><c:set var="end" value="${totalPages}" /></c:if>

    <nav aria-label="Page navigation">
        <ul class="pagination justify-content-center">

            <%-- Nút First & Previous --%>
            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                <a class="page-link" href="?page=1${extraParams}" aria-label="First">&laquo;</a>
            </li>
            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                <a class="page-link" href="?page=${currentPage - 1}${extraParams}" aria-label="Previous">&lsaquo;</a>
            </li>

            <%-- Các trang ở giữa --%>
            <c:forEach begin="${begin}" end="${end}" var="i">
                <li class="page-item ${i == currentPage ? 'active' : ''}">
                    <a class="page-link" href="?page=${i}${extraParams}">${i}</a>
                </li>
            </c:forEach>

            <%-- Nút Next & Last --%>
            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                <a class="page-link" href="?page=${currentPage + 1}${extraParams}" aria-label="Next">&rsaquo;</a>
            </li>
            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                <a class="page-link" href="?page=${totalPages}${extraParams}" aria-label="Last">&raquo;</a>
            </li>

        </ul>
    </nav>
</c:if>