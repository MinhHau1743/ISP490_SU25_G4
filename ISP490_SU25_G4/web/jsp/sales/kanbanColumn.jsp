<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- This file is included by listCustomer.jsp to render a single Kanban column --%>
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
                <h3 class="card-title">${customer.name}</h3>
                
                <div class="card-info-row">
                    <i data-feather="phone"></i>
                    <span>${not empty customer.primaryContactPhone ? customer.primaryContactPhone : 'Chưa có SĐT'}</span>
                </div>
                
                <%-- THIS IS THE IMPORTANT PART FOR THE ADDRESS --%>
                <div class="card-info-row">
                    <i data-feather="map-pin"></i>
                    <%-- It displays the fullAddress property prepared by the DAO --%>
                    <span>${not empty customer.fullAddress ? customer.fullAddress : 'Chưa có địa chỉ'}</span>
                </div>
                
                <div class="card-footer">
                    <div class="card-assignees">
                        <c:forEach var="assignee" items="${customer.assignedUsers}">
                            <img src="${assignee.avatarUrl}" title="${assignee.fullName}">
                        </c:forEach>
                    </div>
                    <div class="card-actions">
                        <a href="${BASE_URL}/viewCustomer?id=${customer.id}" title="Xem chi tiết"><i data-feather="eye"></i></a>
                        <a href="${BASE_URL}/editCustomer?id=${customer.id}" title="Sửa thông tin"><i data-feather="edit-2"></i></a>
                        <a href="#" class="delete-trigger-btn" data-id="${customer.id}" data-name="${customer.name}" title="Xóa"><i data-feather="trash-2"></i></a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
