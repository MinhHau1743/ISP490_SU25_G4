package vn.edu.fpt.common;

import vn.edu.fpt.dao.NotificationDAO;
import vn.edu.fpt.model.Campaign;
import vn.edu.fpt.model.Contract;
import vn.edu.fpt.model.Enterprise;
import vn.edu.fpt.model.Feedback;
import vn.edu.fpt.model.Notification;
import vn.edu.fpt.model.TechnicalRequest;
import vn.edu.fpt.model.User;

public class NotificationService {

    private static final NotificationDAO notificationDAO = new NotificationDAO();

    private NotificationService() {}

    private static void createNotification(String title, String message, String linkUrl, String type, User creator) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setLinkUrl(linkUrl);
        notification.setNotificationType(type);
        if (creator != null) {
            notification.setCreatedById(creator.getId());
        }
        notificationDAO.addNotification(notification);
    }

    // 1. Thông báo cho Khách hàng (Enterprise)
    public static void notifyNewEnterprise(User creator, Enterprise enterprise) {
        String title = "Khách hàng mới";
        String message = String.format("%s vừa thêm mới khách hàng: %s.", creator.getFullName(), enterprise.getName());
        // SỬA LẠI URL: Dùng "customer/view" thay vì "enterprise?action=detail"
        String linkUrl = String.format("customer/view?id=%d", enterprise.getId());
        createNotification(title, message, linkUrl, "ENTERPRISE", creator);
    }

    public static void notifyUpdateEnterprise(User updater, Enterprise enterprise) {
        String title = "Cập nhật khách hàng";
        String message = String.format("%s vừa cập nhật thông tin khách hàng: %s.", updater.getFullName(), enterprise.getName());
        // SỬA LẠI URL: Dùng "customer/view"
        String linkUrl = String.format("customer/view?id=%d", enterprise.getId());
        createNotification(title, message, linkUrl, "ENTERPRISE", updater);
    }

    // 2. Thông báo cho Hợp đồng (Contract)
    public static void notifyNewContract(User creator, Contract contract, String enterpriseName) {
        String title = "Hợp đồng mới";
        String message = String.format("%s vừa tạo hợp đồng mới %s cho khách hàng %s.", creator.getFullName(), contract.getContractCode(), enterpriseName);
        // SỬA LẠI URL: Dùng action "view" thay vì "detail"
        String linkUrl = String.format("contract?action=view&id=%d", contract.getId());
        createNotification(title, message, linkUrl, "CONTRACT", creator);
    }

    public static void notifyUpdateContract(User updater, Contract contract, String enterpriseName) {
        String title = "Cập nhật hợp đồng";
        String message = String.format("%s vừa cập nhật hợp đồng %s của khách hàng %s.", updater.getFullName(), contract.getContractCode(), enterpriseName);
        // SỬA LẠI URL: Dùng action "view"
        String linkUrl = String.format("contract?action=view&id=%d", contract.getId());
        createNotification(title, message, linkUrl, "CONTRACT", updater);
    }

    // 3. Thông báo cho Yêu cầu kỹ thuật (Technical Request / Ticket)
    public static void notifyNewTechnicalRequest(User creator, TechnicalRequest request, String enterpriseName) {
        String title = "Yêu cầu kỹ thuật mới";
        String message = String.format("%s vừa tạo yêu cầu kỹ thuật '%s' cho %s.", creator.getFullName(), request.getTitle(), enterpriseName);
        // SỬA LẠI URL: Dùng "ticket?action=view" thay vì "technical-request?action=detail"
        String linkUrl = String.format("ticket?action=view&id=%d", request.getId());
        createNotification(title, message, linkUrl, "TECH_REQUEST", creator);
    }
     public static void notifyUpdateTechnicalRequest(User updater, TechnicalRequest request, String enterpriseName) {
        String title = "Cập nhật Yêu cầu kỹ thuật";
        String message = String.format("%s vừa cập nhật yêu cầu '%s' cho %s.", updater.getFullName(), request.getTitle(), enterpriseName);
        // SỬA LẠI URL: Dùng "ticket?action=view"
        String linkUrl = String.format("ticket?action=view&id=%d", request.getId());
        createNotification(title, message, linkUrl, "TECH_REQUEST", updater);
    }

    // 4. Thông báo cho Chiến dịch (Campaign)
    public static void notifyNewCampaign(User creator, Campaign campaign) {
        String title = "Chiến dịch mới";
        String message = String.format("%s vừa khởi tạo chiến dịch mới: %s.", creator.getFullName(), campaign.getName());
        // SỬA LẠI URL: Dùng "view-campaign"
        String linkUrl = String.format("view-campaign?id=%d", campaign.getCampaignId());
        createNotification(title, message, linkUrl, "CAMPAIGN", creator);
    }
     public static void notifyUpdateCampaign(User updater, Campaign campaign) {
        String title = "Cập nhật chiến dịch";
        String message = String.format("%s vừa cập nhật chiến dịch: %s.", updater.getFullName(), campaign.getName());
        // SỬA LẠI URL: Dùng "view-campaign"
        String linkUrl = String.format("view-campaign?id=%d", campaign.getCampaignId());
        createNotification(title, message, linkUrl, "CAMPAIGN", updater);
    }


    // 5. Thông báo cho Phản hồi (Feedback)
    public static void notifyNewFeedback(User creator, Feedback feedback, String enterpriseName) {
        String title = "Phản hồi mới từ khách hàng";
        String message;
        
        if (creator != null) {
            message = String.format("%s vừa ghi nhận một phản hồi mới từ khách hàng %s.", creator.getFullName(), enterpriseName);
        } else {
            message = String.format("Hệ thống vừa ghi nhận một phản hồi mới từ khách hàng %s.", enterpriseName);
        }
        
        // URL này đã đúng, không cần sửa
        String linkUrl = String.format("feedback?action=view&id=%d", feedback.getId());
        createNotification(title, message, linkUrl, "FEEDBACK", creator);
    }
}