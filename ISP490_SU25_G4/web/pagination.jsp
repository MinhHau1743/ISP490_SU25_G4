<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> <%-- BẮT BUỘC: Thêm taglib cho functions --%>

<c:if test="${totalPages > 1}">
    <%-- 
        BƯỚC 1: TẠO URL MẪU (TEMPLATE)
        - Tạo một URL cơ bản chứa tất cả các tham số hiện có (keyword, filter,...) NGOẠI TRỪ 'page'.
        - Thêm một tham số 'page' với giá trị là một placeholder "_PAGE_".
        - Biến 'baseUrlTemplate' sẽ chứa URL đã được mã hóa đúng chuẩn, ví dụ: 
          /product?action=list&keyword=abc&page=_PAGE_
    --%>
    <c:url var="baseUrlTemplate" value="${actionUrl}">
        <c:param name="page" value="_PAGE_"/>
        <c:forEach var="p" items="${param}">
            <c:if test="${p.key != 'page' and p.key != 'actionUrl'}">
                <c:forEach var="v" items="${p.value}">
                    <c:param name="${p.key}" value="${v}"/>
                </c:forEach>
            </c:if>
        </c:forEach>
    </c:url>

    <%-- Logic tính toán cửa sổ trượt (sliding window) giữ nguyên --%>
    <c:set var="pagesToShow" value="5"/>
    <c:set var="half" value="${pagesToShow div 2}"/>
    <c:set var="begin" value="${currentPage - half}"/>
    <c:set var="end" value="${currentPage + half}"/>
    <c:if test="${begin < 1}"><c:set var="begin" value="1"/><c:set var="end" value="${pagesToShow}"/></c:if>
    <c:if test="${end > totalPages}">
        <c:set var="end" value="${totalPages}"/>
        <c:set var="begin" value="${totalPages - pagesToShow + 1}"/>
        <c:if test="${begin < 1}"><c:set var="begin" value="1"/></c:if>
    </c:if>

    <nav class="pagination-nav" aria-label="Pagination">
        <ul class="pagination justify-content-center">

            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                <a class="page-link" href="${fn:replace(baseUrlTemplate, '_PAGE_', 1)}" aria-label="Trang đầu">&laquo;</a>
            </li>
            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                <a class="page-link" href="${fn:replace(baseUrlTemplate, '_PAGE_', currentPage - 1)}" aria-label="Trang trước">&lsaquo;</a>
            </li>

            <c:if test="${begin > 1}">
                <li class="page-item">
                    <a class="page-link" href="${fn:replace(baseUrlTemplate, '_PAGE_', 1)}">1</a>
                </li>
                <c:if test="${begin > 2}">
                    <li class="page-item disabled"><span class="page-link">&hellip;</span></li>
                </c:if>
            </c:if>

            <c:forEach var="i" begin="${begin}" end="${end}">
                <li class="page-item ${i == currentPage ? 'active' : ''}">
                    <a class="page-link" href="${fn:replace(baseUrlTemplate, '_PAGE_', i)}">${i}</a>
                </li>
            </c:forEach>

            <c:if test="${end < totalPages}">
                <c:if test="${end < totalPages - 1}">
                    <li class="page-item disabled"><span class="page-link">&hellip;</span></li>
                </c:if>
                <li class="page-item">
                    <a class="page-link" href="${fn:replace(baseUrlTemplate, '_PAGE_', totalPages)}">${totalPages}</a>
                </li>
            </c:if>

            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                <a class="page-link" href="${fn:replace(baseUrlTemplate, '_PAGE_', currentPage + 1)}" aria-label="Trang sau">&rsaquo;</a>
            </li>
            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                <a class="page-link" href="${fn:replace(baseUrlTemplate, '_PAGE_', totalPages)}" aria-label="Trang cuối">&raquo;</a>
            </li>

        </ul>
    </nav>
</c:if>