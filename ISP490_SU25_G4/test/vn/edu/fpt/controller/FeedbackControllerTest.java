// File: test/vn/edu/fpt/controller/FeedbackControllerTest.java
package vn.edu.fpt.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.MockitoJUnitRunner;

import vn.edu.fpt.dao.ContractDAO;
import vn.edu.fpt.dao.FeedbackDAO;
import vn.edu.fpt.model.Contract;
import vn.edu.fpt.model.User;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackControllerTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher dispatcher;

    private FeedbackController controller;

    @Before
    public void setUp() {
        controller = new FeedbackController();
    }

    // =============== CREATE: thành công -> redirect /feedback?action=success ===============
    @Test
    public void testDoPost_Create_Success_Redirect() throws Exception {
        when(request.getParameter("action")).thenReturn("create");
        when(request.getContextPath()).thenReturn("/app");
        // Truyền đủ dữ liệu hợp lệ
        when(request.getParameter("rating")).thenReturn("5");
        when(request.getParameter("comment")).thenReturn("Great service!");
        when(request.getParameter("enterpriseId")).thenReturn("123");
        when(request.getParameter("technicalRequestId")).thenReturn(""); // optional
        when(request.getParameter("contractId")).thenReturn("");         // optional

        // Bắt các lệnh new FeedbackDAO() và stub addFeedback trả true
        try (MockedConstruction<FeedbackDAO> mocked = mockConstruction(
                FeedbackDAO.class,
                (mock, ctx) -> when(mock.addFeedback(any())).thenReturn(true)
        )) {
            controller.doPost(request, response);

            // addFeedback được gọi 1 lần với object bất kỳ
            verify(mocked.constructed().get(0)).addFeedback(any());
            // Redirect đúng
            verify(response).sendRedirect("/app/feedback?action=success");
            // Không forward trang form trong case success
            verify(request, never()).getRequestDispatcher("/jsp/customerSupport/createFeedback.jsp");
        }
    }

    // =============== CREATE: thiếu rating -> forward lại form create (có contractId) ===============
    @Test
    public void testDoPost_Create_MissingRating_ForwardCreateForm() throws Exception {
        when(request.getParameter("action")).thenReturn("create");
        // Lỗi validate: rating = "0"
        when(request.getParameter("rating")).thenReturn("0");
        when(request.getParameter("comment")).thenReturn("...");
        when(request.getParameter("enterpriseId")).thenReturn("321");

        // Chọn nhánh contract để doCreateForm chạy: cần contractId và các DAO tương ứng
        when(request.getParameter("technicalRequestId")).thenReturn("");
        when(request.getParameter("contractId")).thenReturn("88");

        // Dispatcher tới form
        when(request.getRequestDispatcher("/jsp/customerSupport/createFeedback.jsp"))
                .thenReturn(dispatcher);

        try (MockedConstruction<FeedbackDAO> fMock = mockConstruction(
                FeedbackDAO.class,
                (mock, ctx) -> {
                    // form sẽ check đã tồn tại feedback cho contract?
                    when(mock.feedbackExistsForContract(88)).thenReturn(false);
                }
        ); MockedConstruction<ContractDAO> cMock = mockConstruction(
                ContractDAO.class,
                (mock, ctx) -> when(mock.getContractById(88)).thenReturn(new Contract())
        )) {
            controller.doPost(request, response);

            // Vì fail validate, không gọi addFeedback
            // (không verify addFeedback ở đây để tránh UnnecessaryStubbing)
            // Forward về trang tạo
            verify(dispatcher).forward(request, response);
            verify(response, never()).sendRedirect(anyString());
        }
    }

    @Test
    public void testDoPost_Create_MissingEnterpriseId_BadRequest() throws Exception {
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("rating")).thenReturn("4");
        when(request.getParameter("comment")).thenReturn("ok ok");
        // Thiếu enterpriseId (rỗng) -> sẽ sendError 400 và return
        when(request.getParameter("enterpriseId")).thenReturn("");
        when(request.getParameter("technicalRequestId")).thenReturn("");
        when(request.getParameter("contractId")).thenReturn("");

        controller.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Enterprise ID is missing.");
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    public void testDoPost_Create_AddFeedbackFails_ForwardCreateForm_TechnicalRequest() throws Exception {
        when(request.getParameter("action")).thenReturn("create");
        when(request.getParameter("rating")).thenReturn("4");
        when(request.getParameter("comment")).thenReturn("not good enough");
        when(request.getParameter("enterpriseId")).thenReturn("7");
        when(request.getParameter("technicalRequestId")).thenReturn("55");
        when(request.getParameter("contractId")).thenReturn("");

        when(request.getRequestDispatcher("/jsp/customerSupport/createFeedback.jsp"))
                .thenReturn(dispatcher);

        try (MockedConstruction<FeedbackDAO> fMock = mockConstruction(
                FeedbackDAO.class,
                (mock, ctx) -> {
                    // Lần 1 (doCreateSubmit): addFeedback -> false
                    // Lần 2 (doCreateForm): kiểm tra tồn tại theo technicalRequestId
                    when(mock.addFeedback(any())).thenReturn(false);
                    when(mock.feedbackExistsForTechnicalRequest(55)).thenReturn(false);
                }
        ); MockedConstruction<vn.edu.fpt.dao.TechnicalRequestDAO> tMock = mockConstruction(
                vn.edu.fpt.dao.TechnicalRequestDAO.class,
                (mock, ctx) -> when(mock.getTechnicalRequestById(55))
                        .thenReturn(new vn.edu.fpt.model.TechnicalRequest())
        )) {
            controller.doPost(request, response);

            // Vì addFeedback false -> quay lại form create
            verify(dispatcher).forward(request, response);
            verify(response, never()).sendRedirect(anyString());
        }
    }
// =============== UPDATE (editNote): thành công -> redirect view&id=...&noteUpdate=success ===============

    @Test
    public void testDoPost_EditNote_Success_Redirect() throws Exception {
        when(request.getParameter("action")).thenReturn("editNote");
        when(request.getParameter("noteId")).thenReturn("5");
        when(request.getParameter("feedbackId")).thenReturn("12");
        when(request.getParameter("noteText")).thenReturn("Updated note");

        // Cần session và user (controller gọi getSession(false))
        when(request.getSession(false)).thenReturn(session);
        User u = new User();
        u.setId(1);
        when(session.getAttribute("user")).thenReturn(u);

        try (MockedConstruction<FeedbackDAO> mocked = mockConstruction(FeedbackDAO.class)) {
            controller.doPost(request, response);

            // Gọi updateInternalNote đúng tham số
            verify(mocked.constructed().get(0)).updateInternalNote(5, "Updated note");
            // Redirect về trang view feedback
            verify(response).sendRedirect("feedback?action=view&id=12&noteUpdate=success");
        }
    }

    @Test
    public void testDoPost_EditNote_Unauthorized_NoSession() throws Exception {
        when(request.getParameter("action")).thenReturn("editNote");
        when(request.getParameter("noteId")).thenReturn("5");
        when(request.getParameter("feedbackId")).thenReturn("12");
        when(request.getParameter("noteText")).thenReturn("Updated note");

        // Controller gọi getSession(false) -> trả null => 401
        when(request.getSession(false)).thenReturn(null);

        controller.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bạn cần đăng nhập để sửa ghi chú.");
        // Không tạo DAO, không redirect
        verifyNoInteractions(session);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void testDoPost_EditNote_DAOThrows_InternalServerError() throws Exception {
        when(request.getParameter("action")).thenReturn("editNote");
        when(request.getParameter("noteId")).thenReturn("5");
        when(request.getParameter("feedbackId")).thenReturn("12");
        when(request.getParameter("noteText")).thenReturn("Boom!");

        when(request.getSession(false)).thenReturn(session);
        User u = new User();
        u.setId(1);
        when(session.getAttribute("user")).thenReturn(u);

        try (MockedConstruction<FeedbackDAO> mocked = mockConstruction(
                FeedbackDAO.class,
                (mock, ctx) -> doThrow(new RuntimeException("DB down"))
                        .when(mock).updateInternalNote(5, "Boom!")
        )) {
            controller.doPost(request, response);

            // Gọi đúng DAO rồi báo lỗi 500
            verify(mocked.constructed().get(0)).updateInternalNote(5, "Boom!");
            verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể cập nhật ghi chú.");
            verify(response, never()).sendRedirect(anyString());
        }
    }

 @Test
public void testDoPost_EditNote_InvalidNoteId_RedirectInvalidNote() throws Exception {
    when(request.getParameter("action")).thenReturn("editNote");
    when(request.getParameter("noteId")).thenReturn("abc");      // invalid
    when(request.getParameter("feedbackId")).thenReturn("12");   // valid
    when(request.getParameter("noteText")).thenReturn("Updated note");

    try (MockedConstruction<FeedbackDAO> mocked = mockConstruction(FeedbackDAO.class)) {
        controller.doPost(request, response);

        // ĐÚNG hành vi: redirect lỗi invalidNote
        verify(response).sendRedirect("feedback?action=view&id=12&noteError=invalidNote");
        // Không phải 500
        verify(response, never()).sendError(anyInt(), anyString());
        // DAO không được khởi tạo/gọi trong nhánh validate fail
        assertTrue(mocked.constructed().isEmpty());
    }
}

}
