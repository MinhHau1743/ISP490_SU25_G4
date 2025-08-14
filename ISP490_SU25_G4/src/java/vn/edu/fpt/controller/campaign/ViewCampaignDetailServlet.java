/*
 * File: vn.edu.fpt.controller.campaign.ViewCampaignDetailServlet.java
 * Description: View-only. Không cập nhật DB. Tự tính trạng thái "Quá hạn" cho hiển thị.
 */
package vn.edu.fpt.controller.campaign;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.edu.fpt.dao.CampaignDAO;
import vn.edu.fpt.dao.MaintenanceScheduleDAO;
import vn.edu.fpt.model.Campaign;
import vn.edu.fpt.model.MaintenanceSchedule;

@WebServlet(name = "ViewCampaignDetailServlet", urlPatterns = {"/view-campaign"})
public class ViewCampaignDetailServlet extends HttpServlet {

    private static final ZoneId VN_TZ = ZoneId.of("Asia/Bangkok");
    private static final DateTimeFormatter DMY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private CampaignDAO campaignDAO;
    private MaintenanceScheduleDAO maintenanceScheduleDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        campaignDAO = new CampaignDAO();
        maintenanceScheduleDAO = new MaintenanceScheduleDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String campaignIdStr = request.getParameter("id");
        int campaignId;
        try {
            campaignId = Integer.parseInt(campaignIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("campaign", null);
            request.getRequestDispatcher("/jsp/customerSupport/viewCampaignDetails.jsp")
                    .forward(request, response);
            return;
        }

        // 1) Lấy campaign
        Campaign campaign = campaignDAO.getCampaignById(campaignId);
        if (campaign == null) {
            request.setAttribute("campaign", null);
            request.getRequestDispatcher("/jsp/customerSupport/viewCampaignDetails.jsp")
                    .forward(request, response);
            return;
        }

        // 2) Lấy lịch đại diện + statusName từ Statuses
        MaintenanceSchedule maintenanceSchedule
                = maintenanceScheduleDAO.getMaintenanceScheduleWithStatusByCampaignId(campaignId);

        // 3) Tự tính "Quá hạn" CHỈ ĐỂ HIỂN THỊ (không ghi DB)
        if (maintenanceSchedule != null) {
            String statusName = maintenanceSchedule.getStatusName() == null
                    ? "" : maintenanceSchedule.getStatusName().trim();

            // Không đè khi đã đóng
            boolean isClosed = statusName.equalsIgnoreCase("Hoàn thành")
                    || statusName.equalsIgnoreCase("Đã hủy");

            if (!isClosed) {
                LocalDate today = LocalDate.now(VN_TZ);
                LocalTime now = LocalTime.now(VN_TZ);

                // Ưu tiên endDate; nếu không có thì dùng scheduledDate
                LocalDate baseDate = maintenanceSchedule.getEndDate();
                if (baseDate == null) {
                    baseDate = maintenanceSchedule.getScheduledDate();
                }

                // Nếu không có endTime, coi như 23:59
                LocalTime effectiveEndTime = maintenanceSchedule.getEndTime() != null
                        ? maintenanceSchedule.getEndTime()
                        : LocalTime.of(23, 59);

                boolean overdue = false;
                if (baseDate != null) {
                    overdue = baseDate.isBefore(today)
                            || (baseDate.isEqual(today) && effectiveEndTime.isBefore(now));
                }

                if (overdue) {
                    maintenanceSchedule.setStatusName("Quá hạn"); // chỉ đổi để hiển thị
                }
            }
        }

        // 4) Chuẩn bị chuỗi ngày để JSP in trực tiếp (tránh fmt:formatDate với LocalDate)
        String scheduledDateStr = null;
        String endDateStr = null;
        if (maintenanceSchedule != null) {
            if (maintenanceSchedule.getScheduledDate() != null) {
                scheduledDateStr = maintenanceSchedule.getScheduledDate().format(DMY);
            }
            if (maintenanceSchedule.getEndDate() != null) { // LocalDate (theo model mới)
                endDateStr = maintenanceSchedule.getEndDate().format(DMY);
            }
        }

        // 5) Đặt attribute và forward
        request.setAttribute("campaign", campaign);
        request.setAttribute("maintenanceSchedule", maintenanceSchedule);
        request.setAttribute("scheduledDateStr", scheduledDateStr);
        request.setAttribute("endDateStr", endDateStr);

        request.getRequestDispatcher("/jsp/customerSupport/viewCampaignDetails.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "View campaign details + representative schedule (read-only, computes overdue for display).";
    }
}
