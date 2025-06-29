// File: vn/edu/fpt/controller/TicketController.java
package vn.edu.fpt.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.fpt.dao.TechnicalRequestDAO;
import vn.edu.fpt.model.TechnicalRequest;
import vn.edu.fpt.model.TechnicalRequestDevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "TicketController", urlPatterns = {"/ticket"})
public class TicketController extends HttpServlet {

    private TechnicalRequestDAO dao;

    @Override
    public void init() {
        dao = new TechnicalRequestDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        switch (action) {
            case "create":
                showCreateForm(request, response);
                break;
            case "view":
                viewTicket(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "list":
            default:
                listTickets(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            createTicket(request, response);
        } else if ("update".equals(action)) {
            updateTicket(request, response);
        }
    }

    private void updateTicketAssignment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String status = request.getParameter("status");
            String priority = request.getParameter("priority");
            int employeeId = Integer.parseInt(request.getParameter("employeeId"));
            boolean isBillable = Boolean.parseBoolean(request.getParameter("isBillable"));

            double estimatedCost = 0;
            if (isBillable) {
                String amountStr = request.getParameter("amount");
                estimatedCost = (amountStr == null || amountStr.isEmpty()) ? 0 : Double.parseDouble(amountStr);
            }

            // Chuyển đổi giá trị priority từ tiếng Việt sang giá trị của DB
            String priorityDb = "medium";
            if ("Cao".equals(priority)) {
                priorityDb = "high";
            } else if ("Khẩn cấp".equals(priority)) {
                priorityDb = "critical";
            } else if ("Thấp".equals(priority)) {
                priorityDb = "low";
            }

            boolean success = dao.updateTicketAssignment(id, status, priorityDb, employeeId, isBillable, estimatedCost);

            // Sau khi cập nhật, chuyển hướng về trang xem chi tiết
            response.sendRedirect("ticket?action=view&id=" + id + "&update=" + (success ? "success" : "failed"));

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ticket?action=edit&id=" + request.getParameter("id") + "&error=true");
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            TechnicalRequest existingTicket = dao.getTechnicalRequestById(id);

            if (existingTicket != null) {
                request.setAttribute("ticket", existingTicket);
                request.setAttribute("customerList", dao.getAllEnterprises());
                request.setAttribute("employeeList", dao.getAllTechnicians());
                request.setAttribute("serviceList", dao.getAllServices());

                request.getRequestDispatcher("/jsp/customerSupport/editTransaction.jsp").forward(request, response);
            } else {
                response.sendRedirect("ticket?action=list&error=notFound");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ticket?action=list&error=generic");
        }
    }

    private void updateTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            TechnicalRequest ticketToUpdate = new TechnicalRequest();
            ticketToUpdate.setId(Integer.parseInt(request.getParameter("id")));
            ticketToUpdate.setEnterpriseId(Integer.parseInt(request.getParameter("enterpriseId")));
            ticketToUpdate.setServiceId(Integer.parseInt(request.getParameter("serviceId")));
            ticketToUpdate.setAssignedToId(Integer.parseInt(request.getParameter("employeeId")));

            String contractCode = request.getParameter("contractCode");
            if (contractCode != null && !contractCode.trim().isEmpty()) {
                ticketToUpdate.setContractId(dao.getContractIdByCode(contractCode));
            }

            String description = request.getParameter("description");
            ticketToUpdate.setDescription(description);
            if (description != null && description.length() > 100) {
                ticketToUpdate.setTitle(description.substring(0, 100) + "...");
            } else {
                ticketToUpdate.setTitle(description);
            }

            String priorityVie = request.getParameter("priority");
            String priorityDb = "medium";
            if ("Cao".equals(priorityVie)) {
                priorityDb = "high";
            } else if ("Khẩn cấp".equals(priorityVie)) {
                priorityDb = "critical";
            }
            ticketToUpdate.setPriority(priorityDb);

            ticketToUpdate.setStatus(request.getParameter("status"));

            ticketToUpdate.setIsBillable(Boolean.parseBoolean(request.getParameter("isBillable")));
            if (ticketToUpdate.isIsBillable()) {
                String amountStr = request.getParameter("amount");
                ticketToUpdate.setEstimatedCost(amountStr == null || amountStr.isEmpty() ? 0 : Double.parseDouble(amountStr));
            } else {
                ticketToUpdate.setEstimatedCost(0);
            }

            List<TechnicalRequestDevice> devices = new ArrayList<>();
            int i = 1;
            while (request.getParameter("deviceName_" + i) != null) {
                String deviceName = request.getParameter("deviceName_" + i);
                if (deviceName != null && !deviceName.trim().isEmpty()) {
                    String serial = request.getParameter("deviceSerial_" + i);
                    String note = request.getParameter("deviceNote_" + i);
                    devices.add(new TechnicalRequestDevice(deviceName, serial, note));
                }
                i++;
            }

            boolean success = dao.updateTechnicalRequest(ticketToUpdate, devices);
            response.sendRedirect("ticket?action=list&update=" + (success ? "success" : "failed"));
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ticket?action=edit&id=" + request.getParameter("id") + "&error=true");
        }
    }

    private void listTickets(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<TechnicalRequest> transactionList = dao.getAllTechnicalRequests();
        request.setAttribute("transactions", transactionList);
        request.getRequestDispatcher("/jsp/customerSupport/listTransaction.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("customerList", dao.getAllEnterprises());
        request.setAttribute("employeeList", dao.getAllTechnicians());
        request.setAttribute("serviceList", dao.getAllServices());
        request.getRequestDispatcher("/jsp/customerSupport/createTicket.jsp").forward(request, response);
    }

    private void createTicket(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        try {
            TechnicalRequest newRequest = new TechnicalRequest();
            newRequest.setReporterId(1); // Gán cứng người tạo
            newRequest.setEnterpriseId(Integer.parseInt(request.getParameter("enterpriseId")));
            newRequest.setServiceId(Integer.parseInt(request.getParameter("serviceId")));
            newRequest.setAssignedToId(Integer.parseInt(request.getParameter("employeeId")));

            String contractCode = request.getParameter("contractCode");
            if (contractCode != null && !contractCode.trim().isEmpty()) {
                newRequest.setContractId(dao.getContractIdByCode(contractCode));
            }

            String description = request.getParameter("description");
            newRequest.setDescription(description);
            if (description != null && description.length() > 100) {
                newRequest.setTitle(description.substring(0, 100) + "...");
            } else {
                newRequest.setTitle(description);
            }

            String priorityVie = request.getParameter("priority");
            String priorityDb = "medium";
            if ("Cao".equals(priorityVie)) {
                priorityDb = "high";
            } else if ("Khẩn cấp".equals(priorityVie)) {
                priorityDb = "critical";
            }
            newRequest.setPriority(priorityDb);

            newRequest.setIsBillable(Boolean.parseBoolean(request.getParameter("isBillable")));
            if (newRequest.isIsBillable()) {
                String amountStr = request.getParameter("amount");
                newRequest.setEstimatedCost(amountStr == null || amountStr.isEmpty() ? 0 : Double.parseDouble(amountStr));
            } else {
                newRequest.setEstimatedCost(0);
            }

            List<TechnicalRequestDevice> devices = new ArrayList<>();
            int i = 1;
            while (request.getParameter("deviceName_" + i) != null) {
                String deviceName = request.getParameter("deviceName_" + i);
                if (deviceName != null && !deviceName.trim().isEmpty()) {
                    String serial = request.getParameter("deviceSerial_" + i);
                    String note = request.getParameter("deviceNote_" + i);
                    devices.add(new TechnicalRequestDevice(deviceName, serial, note));
                }
                i++;
            }

            boolean success = dao.createTechnicalRequest(newRequest, devices);
            response.sendRedirect("ticket?action=list&create=" + (success ? "success" : "failed"));
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ticket?action=create&error=unknown");
        }
    }

    private void viewTicket(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            TechnicalRequest ticket = dao.getTechnicalRequestById(id);

            if (ticket != null) {
                request.setAttribute("ticket", ticket);
                // SỬA LẠI Ở ĐÂY: Đảm bảo chuyển hướng đến đúng file viewTicket.jsp
                request.getRequestDispatcher("/jsp/customerSupport/viewTransaction.jsp").forward(request, response);
            } else {
                response.sendRedirect("ticket?action=list&error=notFound");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("ticket?action=list&error=invalidId");
        }
    }

}
