<%--
    Document   : kanbanColumn
    Description: A reusable component to display a single column in the Kanban board.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>

<c:set var="columnItems" value="${customerColumns[param.columnKey]}"/>
<c:set var="BASE_URL" value="${pageContext.request.contextPath}" />

<div class="kanban-column ${param.columnKey}">
    <div class="kanban-column-header">
        <span class="status-dot"></span>
        <h2 class="column-title">${param.columnTitle}</h2>
        <span class="column-count">${not empty columnItems ? columnItems.size() : 0}</span>
    </div>
    <div class="kanban-cards">
        <c:forEach var="customer" items="${columnItems}">
            <div class="customer-kanban-card">
                
                <%-- Customer Avatar and Name --%>
                <div class="card-main-info" style="display: flex; align-items: center; gap: 12px; margin-bottom: 12px;">
                    <div class="card-avatar">
                        <c:choose>
                            <c:when test="${not empty customer.avatarUrl}">
                                <img src="${BASE_URL}/${customer.avatarUrl}" alt="Avatar" style="width: 40px; height: 40px; border-radius: 50%; object-fit: cover;">
                            </c:when>
                            <c:otherwise>
                                <div class="avatar-placeholder" style="width: 40px; height: 40px; border-radius: 50%; background-color: #e0f2fe; color: #0c4a6e; display: flex; align-items: center; justify-content: center; font-weight: 600;">
                                    <span><c:out value="${customer.name.substring(0,1)}"/></span>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <h3 class="card-title" style="margin: 0; font-size: 1rem; font-weight: 600;"><c:out value="${customer.name}"/></h3>
                </div>

                <%-- Contact Info --%>
                <div class="card-info-row"><i data-feather="phone"></i><span><c:out value="${not empty customer.primaryContactPhone ? customer.primaryContactPhone : 'Chưa có SĐT'}"/></span></div>
                <div class="card-info-row"><i data-feather="map-pin"></i><span><c:out value="${not empty customer.fullAddress ? customer.fullAddress : 'Chưa có địa chỉ'}"/></span></div>
                
                <%-- Card Footer with Assigned Users' Avatars --%>
                <div class="card-footer">
                    <div class="card-assignees">
                        <c:forEach var="assignee" items="${customer.assignedUsers}">
                            <c:choose>
                                <c:when test="${not empty assignee.avatarUrl}">
                                    <img src="${BASE_URL}/${assignee.avatarUrl}" 
                                         title="<c:out value="${assignee.fullName}"/>" 
                                         class="assignee-avatar"
                                         onerror="this.onerror=null; this.src='https://placehold.co/24x24/fee2e2/991b1b?text=Lỗi'">
                                </c:when>
                                <c:otherwise>
                                    <c:set var="initial" value="${not empty assignee.firstName ? assignee.firstName.substring(0,1) : 'A'}"/>
                                    <img src="https://placehold.co/24x24/E0F7FA/00796B?text=<c:out value='${initial}'/>" 
                                         title="<c:out value="${assignee.fullName}"/>" 
                                         class="assignee-avatar">
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </div>
                    <div class="card-actions">
                        <a href="${BASE_URL}/viewCustomer?id=${customer.id}" title="Xem"><i data-feather="eye"></i></a>
                        <a href="${BASE_URL}/editCustomer?id=${customer.id}" title="Sửa"><i data-feather="edit-2"></i></a>
                        <a href="#" class="delete-trigger-btn" data-id="${customer.id}" data-name="<c:out value='${customer.name}'/>" title="Xóa"><i data-feather="trash-2"></i></a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
