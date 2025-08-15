<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${totalPages > 1}">
    <!-- Base URL luôn context-relative; n?u không truy?n thì dùng /list-campaign -->
    <c:set var="baseAction" value="${empty param.actionUrl ? '/list-campaign' : param.actionUrl}"/>

    <!-- Sliding window -->
    <c:set var="pagesToShow" value="5"/>
    <c:set var="half" value="${pagesToShow div 2}"/>
    <c:set var="begin" value="${currentPage - half}"/>
    <c:set var="end"   value="${currentPage + half}"/>
    <c:if test="${begin < 1}"><c:set var="begin" value="1"/><c:set var="end" value="${pagesToShow}"/></c:if>
    <c:if test="${end > totalPages}">
        <c:set var="end" value="${totalPages}"/>
        <c:set var="begin" value="${totalPages - pagesToShow + 1}"/>
        <c:if test="${begin < 1}"><c:set var="begin" value="1"/></c:if>
    </c:if>

    <nav class="pagination-nav" aria-label="Pagination">
        <ul class="pagination justify-content-center">

            <!-- First -->
            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                <c:url var="firstUrl" value="${baseAction}">
                    <c:param name="page" value="1"/>
                    <c:forEach var="p" items="${param}">
                        <c:if test="${p.key != 'page' && p.key != 'actionUrl'}">
                            <c:forEach var="v" items="${p.value}">
                                <c:param name="${p.key}" value="${v}"/>
                            </c:forEach>
                        </c:if>
                    </c:forEach>
                </c:url>
                <a class="page-link" href="${firstUrl}" aria-label="Trang ??u">&laquo;</a>
            </li>

            <!-- Prev -->
            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                <c:url var="prevUrl" value="${baseAction}">
                    <c:param name="page" value="${currentPage - 1}"/>
                    <c:forEach var="p" items="${param}">
                        <c:if test="${p.key != 'page' && p.key != 'actionUrl'}">
                            <c:forEach var="v" items="${p.value}">
                                <c:param name="${p.key}" value="${v}"/>
                            </c:forEach>
                        </c:if>
                    </c:forEach>
                </c:url>
                <a class="page-link" href="${prevUrl}" aria-label="Trang tr??c">&lsaquo;</a>
            </li>

            <!-- 1 ? -->
            <c:if test="${begin > 1}">
                <li class="page-item">
                    <c:url var="p1Url" value="${baseAction}">
                        <c:param name="page" value="1"/>
                        <c:forEach var="p" items="${param}">
                            <c:if test="${p.key != 'page' && p.key != 'actionUrl'}">
                                <c:forEach var="v" items="${p.value}">
                                    <c:param name="${p.key}" value="${v}"/>
                                </c:forEach>
                            </c:if>
                        </c:forEach>
                    </c:url>
                    <a class="page-link" href="${p1Url}">1</a>
                </li>
                <c:if test="${begin > 2}">
                    <li class="page-item disabled"><span class="page-link">?</span></li>
                    </c:if>
                </c:if>

            <!-- Numbers -->
            <c:forEach var="i" begin="${begin}" end="${end}">
                <li class="page-item ${i == currentPage ? 'active' : ''}">
                    <c:url var="numUrl" value="${baseAction}">
                        <c:param name="page" value="${i}"/>
                        <c:forEach var="p" items="${param}">
                            <c:if test="${p.key != 'page' && p.key != 'actionUrl'}">
                                <c:forEach var="v" items="${p.value}">
                                    <c:param name="${p.key}" value="${v}"/>
                                </c:forEach>
                            </c:if>
                        </c:forEach>
                    </c:url>
                    <a class="page-link" href="${numUrl}">${i}</a>
                </li>
            </c:forEach>

            <!-- ? last -->
            <c:if test="${end < totalPages}">
                <c:if test="${end < totalPages - 1}">
                    <li class="page-item disabled"><span class="page-link">?</span></li>
                    </c:if>
                <li class="page-item">
                    <c:url var="plastUrl" value="${baseAction}">
                        <c:param name="page" value="${totalPages}"/>
                        <c:forEach var="p" items="${param}">
                            <c:if test="${p.key != 'page' && p.key != 'actionUrl'}">
                                <c:forEach var="v" items="${p.value}">
                                    <c:param name="${p.key}" value="${v}"/>
                                </c:forEach>
                            </c:if>
                        </c:forEach>
                    </c:url>
                    <a class="page-link" href="${plastUrl}">${totalPages}</a>
                </li>
            </c:if>

            <!-- Next -->
            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                <c:url var="nextUrl" value="${baseAction}">
                    <c:param name="page" value="${currentPage + 1}"/>
                    <c:forEach var="p" items="${param}">
                        <c:if test="${p.key != 'page' && p.key != 'actionUrl'}">
                            <c:forEach var="v" items="${p.value}">
                                <c:param name="${p.key}" value="${v}"/>
                            </c:forEach>
                        </c:if>
                    </c:forEach>
                </c:url>
                <a class="page-link" href="${nextUrl}" aria-label="Trang sau">&rsaquo;</a>
            </li>

            <!-- Last -->
            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                <c:url var="lastUrl" value="${baseAction}">
                    <c:param name="page" value="${totalPages}"/>
                    <c:forEach var="p" items="${param}">
                        <c:if test="${p.key != 'page' && p.key != 'actionUrl'}">
                            <c:forEach var="v" items="${p.value}">
                                <c:param name="${p.key}" value="${v}"/>
                            </c:forEach>
                        </c:if>
                    </c:forEach>
                </c:url>
                <a class="page-link" href="${lastUrl}" aria-label="Trang cu?i">&raquo;</a>
            </li>

        </ul>
    </nav>
</c:if>
