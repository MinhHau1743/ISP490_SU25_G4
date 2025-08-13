//package vn.edu.fpt.controller;
//
//import java.io.IOException;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.MultipartConfig;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.Part;
//import java.io.File;
//import java.nio.file.Paths;
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import vn.edu.fpt.dao.CampaignDAO;
//import vn.edu.fpt.model.Campaign;
//import vn.edu.fpt.model.User; // Import User nếu bạn cần lấy User ID từ session
//
//@WebServlet("/update-campaign")
//// THAY ĐỔI 1: Thêm annotation @MultipartConfig để xử lý yêu cầu multipart (upload file)
//@MultipartConfig(
//    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
//    maxFileSize = 1024 * 1024 * 10,      // 10 MB
//    maxRequestSize = 1024 * 1024 * 15    // 15 MB
//)
//public class UpdateCampaignServlet extends HttpServlet {
//
//    private static final long serialVersionUID = 1L;
//    private CampaignDAO campaignDAO;
//    
//    // THAY ĐỔI 2: Định nghĩa thư mục để lưu file upload
//    private static final String UPLOAD_DIRECTORY = "uploads";
//
//    @Override
//    public void init() throws ServletException {
//        super.init();
//        campaignDAO = new CampaignDAO();
//    }
//
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        request.setCharacterEncoding("UTF-8");
//        response.setCharacterEncoding("UTF-8");
//
//        String campaignIdParam = request.getParameter("campaignId");
//        String name = request.getParameter("name");
//        String startDateStr = request.getParameter("startDate");
//        String endDateStr = request.getParameter("endDate");
//        String status = request.getParameter("status");
//        String description = request.getParameter("description");
//        
//        // Default redirect URL (in case of immediate errors or fallback)
//        String redirectUrl = request.getContextPath() + "/list-campaign"; // Mặc định chuyển về trang danh sách
//
//        try {
//            int campaignId = Integer.parseInt(campaignIdParam);
//
//            Campaign existingCampaign = campaignDAO.getCampaignById(campaignId);
//
//            if (existingCampaign == null) {
//                redirectUrl += "?error=" + java.net.URLEncoder.encode("Chiến dịch không tồn tại để cập nhật.", "UTF-8");
//            } else {
//                
//                // =============================================================
//                // THAY ĐỔI 3: LOGIC XỬ LÝ UPLOAD FILE
//                // =============================================================
//                
//                // Lấy đường dẫn tuyệt đối của thư mục web, sau đó nối với thư mục upload
//                String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
//                
//                // Tạo thư mục nếu nó chưa tồn tại
//                File uploadDir = new File(uploadPath);
//                if (!uploadDir.exists()) {
//                    uploadDir.mkdir();
//                }
//
//                // Lấy phần file từ request
//                Part filePart = request.getPart("attachmentFile");
//                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
//
//                // Kiểm tra xem người dùng có tải lên file mới không
//                if (fileName != null && !fileName.isEmpty()) {
//                    // Tạo đường dẫn đầy đủ đến file sẽ lưu
//                    String filePath = uploadPath + File.separator + fileName;
//                    
//                    // Ghi file lên server
//                    filePart.write(filePath);
//                    
//                    // Cập nhật tên file mới vào đối tượng campaign
//                    existingCampaign.setAttachmentFileName(fileName); 
//                    // Lưu ý: Bạn cần thêm trường attachmentFileName vào model Campaign và CSDL
//                }
//                // Nếu người dùng không upload file mới, trường attachmentFileName của existingCampaign sẽ giữ nguyên giá trị cũ.
//                
//                // =============================================================
//                // KẾT THÚC LOGIC XỬ LÝ UPLOAD FILE
//                // =============================================================
//
//                existingCampaign.setName(name);
//                existingCampaign.setStatus(status);
//                existingCampaign.setDescription(description);
//
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                Date parsedStartDate = sdf.parse(startDateStr);
//                Date parsedEndDate = sdf.parse(endDateStr);
//
//                existingCampaign.setStartDate(parsedStartDate);
//                existingCampaign.setEndDate(parsedEndDate);
//                
//                // Kiểm tra logic ngày kết thúc không được trước ngày bắt đầu
//                if (existingCampaign.getEndDate().before(existingCampaign.getStartDate())) {
//                    redirectUrl = request.getContextPath() + "/edit-campaign?id=" + campaignId + "&error=" + 
//                                    java.net.URLEncoder.encode("Ngày kết thúc không được trước ngày bắt đầu.", "UTF-8");
//                    response.sendRedirect(redirectUrl);
//                    return; // Quan trọng: Thoát khỏi phương thức sau khi chuyển hướng
//                }
//
//                // Lấy ID người dùng hiện tại từ session (ví dụ)
//                // GIẢ ĐỊNH: Bạn đã lưu User ID trong session khi người dùng đăng nhập
//                Integer loggedInUserId = 1; // TẠM THỜI HARDCODE, THAY BẰNG LẤY TỪ SESSION
//                
//                // QUAN TRỌNG: GÁN giá trị cho updatedBy
//                existingCampaign.setUpdatedBy(loggedInUserId); // Gán ID người dùng cập nhật
//
//                // Cập nhật timestamp cho updatedAt
//                existingCampaign.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
//
//                boolean success = campaignDAO.updateCampaign(existingCampaign);
//
//                if (success) {
//                    // Chuyển hướng về trang danh sách chiến dịch với thông báo thành công
//                    redirectUrl = request.getContextPath() + "/list-campaign?success=" + 
//                                    java.net.URLEncoder.encode("Cập nhật chiến dịch thành công!", "UTF-8");
//                } else {
//                    // Chuyển hướng về trang chỉnh sửa với thông báo lỗi nếu cập nhật thất bại
//                    redirectUrl = request.getContextPath() + "/edit-campaign?id=" + campaignId + "&error=" + 
//                                    java.net.URLEncoder.encode("Cập nhật chiến dịch thất bại. Vui lòng thử lại.", "UTF-8");
//                }
//            }
//        } catch (NumberFormatException e) {
//            redirectUrl = request.getContextPath() + "/list-campaign?error=" + 
//                            java.net.URLEncoder.encode("ID chiến dịch không hợp lệ.", "UTF-8");
//        } catch (java.text.ParseException e) {
//            // Chuyển hướng về trang chỉnh sửa nếu định dạng ngày không hợp lệ
//            redirectUrl = request.getContextPath() + "/edit-campaign?id=" + campaignIdParam + "&error=" + 
//                            java.net.URLEncoder.encode("Định dạng ngày không hợp lệ. Vui lòng sử dụng YYYY-MM-DD.", "UTF-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//            redirectUrl = request.getContextPath() + "/list-campaign?error=" + 
//                            java.net.URLEncoder.encode("Có lỗi xảy ra trong quá trình cập nhật: " + e.getMessage(), "UTF-8");
//        }
//
//        response.sendRedirect(redirectUrl);
//    }
//}