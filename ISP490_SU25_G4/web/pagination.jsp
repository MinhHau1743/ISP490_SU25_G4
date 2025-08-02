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
    <%-- Điều chỉnh sliding window cho 5 trang thay vì 3 để hiển thị nhiều trang hơn --%>
    <c:set var="pagesToShow" value="5" /> <%-- Số lượng nút trang muốn hiển thị --%>
    <c:set var="halfPagesToShow" value="${pagesToShow div 2}" />

    <c:set var="begin" value="${currentPage - halfPagesToShow}" />
    <c:set var="end" value="${currentPage + halfPagesToShow}" />

    <%-- Điều chỉnh cho các trường hợp ở đầu và cuối --%>
    <c:if test="${begin < 1}">
        <c:set var="begin" value="1" />
        <c:set var="end" value="${pagesToShow}" />
    </c:if>
    <c:if test="${end > totalPages}">
        <c:set var="end" value="${totalPages}" />
        <c:set var="begin" value="${totalPages - pagesToShow + 1}" />
        <c:if test="${begin < 1}"><c:set var="begin" value="1" /></c:if>
    </c:if>

    <nav aria-label="Page navigation">
        <ul class="pagination justify-content-center">

            <%-- Nút First --%>
            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                <a class="page-link" href="?page=1${extraParams}" aria-label="First">
                    <span aria-hidden="true">&laquo;</span>
                </a>
            </li>
            <%-- Nút Previous --%>
            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                <a class="page-link" href="?page=${currentPage - 1}${extraParams}" aria-label="Previous">
                    <span aria-hidden="true">&lsaquo;</span>
                </a>
            </li>

            <%-- Các trang ở giữa --%>
            <c:forEach begin="${begin}" end="${end}" var="i">
                <li class="page-item ${i == currentPage ? 'active' : ''}">
                    <a class="page-link" href="?page=${i}${extraParams}">${i}</a>
                </li>
            </c:forEach>

            <%-- Nút Next --%>
            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                <a class="page-link" href="?page=${currentPage + 1}${extraParams}" aria-label="Next">
                    <span aria-hidden="true">&rsaquo;</span>
                </a>
            </li>
            <%-- Nút Last --%>
            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                <a class="page-link" href="?page=${totalPages}${extraParams}" aria-label="Last">
                    <span aria-hidden="true">&raquo;</span>
                </a>
            </li>

        </ul>
    </nav>
</c:if>