<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Gửi Phản Hồi - DPCRM</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>

        <style>
            html, body {
                height: 100%;
                font-family: 'Inter', sans-serif;
                margin: 0;
                background-color: #f9fafb;
                display: flex;
                justify-content: center;
                align-items: flex-start;
                padding-top: 48px;
                box-sizing: border-box;
            }
            .feedback-wrapper {
                max-width: 600px;
                width: 100%;
                padding: 0 16px;
            }
            .feedback-container {
                background-color: white;
                padding: 40px;
                border-radius: 16px;
                border: 1px solid #e5e7eb;
                box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.07), 0 4px 6px -4px rgb(0 0 0 / 0.1);
                text-align: center;
            }
            .feedback-header {
                margin-bottom: 32px;
            }
            .feedback-header .icon {
                width: 56px;
                height: 56px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                background-color: #dbeafe;
                color: #2563eb;
                border-radius: 50%;
                margin-bottom: 16px;
            }
            .feedback-header h1 {
                font-size: 24px;
                font-weight: 700;
                color: #111827;
                margin: 0 0 8px 0;
            }
            .feedback-header p {
                font-size: 16px;
                color: #6b7280;
                margin: 0;
                line-height: 1.6;
            }
            .feedback-form .form-group {
                margin-bottom: 24px;
                text-align: left;
            }
            .feedback-form label {
                display: block;
                font-weight: 600;
                margin-bottom: 10px;
                color: #374151;
                font-size: 14px;
            }
            .star-rating-group {
                text-align: center;
            }
            .star-rating {
                display: flex;
                justify-content: center;
                gap: 16px;
                margin-bottom: 12px;
            }
            .star-rating .star {
                cursor: pointer;
                color: #d1d5db;
                transition: color 0.2s ease, transform 0.2s ease;
            }
            .star-rating .star:hover {
                transform: scale(1.2);
            }
            .star-rating .star.selected {
                color: #facc15;
            }
            #rating-caption {
                font-weight: 600;
                color: #6b7280;
                min-height: 24px;
            }
            .feedback-form textarea {
                width: 100%;
                padding: 12px 16px;
                border: 1px solid #d1d5db;
                border-radius: 8px;
                font-family: 'Inter', sans-serif;
                font-size: 16px;
                box-sizing: border-box;
                resize: vertical;
                transition: border-color 0.2s, box-shadow 0.2s;
            }
            .feedback-form textarea:focus {
                outline: none;
                border-color: #3b82f6;
                box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
            }
            .btn-submit {
                width: 100%;
                padding: 14px;
                font-size: 16px;
                font-weight: 600;
                border: none;
                border-radius: 8px;
                background-color: #3b82f6;
                color: white;
                cursor: pointer;
                transition: background-color 0.2s, transform 0.1s;
            }
            .btn-submit:hover {
                background-color: #2563eb;
            }
            .form-control-static {
                padding: 12px 16px;
                background-color: #f9fafb;
                border: 1px solid #e5e7eb;
                border-radius: 8px;
                color: #374151;
            }
        </style>
    </head>
    <body>
        <div class="feedback-wrapper">
            <div class="feedback-container">
                <div class="feedback-header">
                    <div class="icon">
                        <i data-feather="star" style="width: 28px; height: 28px;"></i>
                    </div>
                    <h1>Đánh giá chất lượng dịch vụ</h1>
                    <p>
                        Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi. Vui lòng cho biết cảm nhận của bạn về
                        <%-- HIỂN THỊ ĐỘNG TÙY THEO LOẠI PHẢN HỒI --%>
                        <c:if test="${not empty technicalRequest}">
                            yêu cầu <strong>"${technicalRequest.title}"</strong> của doanh nghiệp <strong>${technicalRequest.enterpriseName}</strong>.
                        </c:if>
                        <c:if test="${not empty contract}">
                            hợp đồng <strong>"${contract.contractCode}"</strong> của doanh nghiệp <strong>${contract.enterpriseName}</strong>.
                        </c:if>
                    </p>
                </div>

                <form action="${pageContext.request.contextPath}/feedback?action=create" method="POST" class="feedback-form">
                    <%-- CÁC INPUT ẨN QUAN TRỌNG ĐỂ GỬI ĐÚNG DỮ LIỆU --%>
                    <input type="hidden" id="rating-value" name="rating" value="0">

                    <%-- Khối input ẩn cho Yêu cầu Kỹ thuật --%>
                    <c:if test="${not empty technicalRequest}">
                        <input type="hidden" name="technicalRequestId" value="${technicalRequest.id}">
                        <input type="hidden" name="enterpriseId" value="${technicalRequest.enterpriseId}">
                    </c:if>

                    <%-- Khối input ẩn cho Hợp đồng --%>
                    <c:if test="${not empty contract}">
                        <input type="hidden" name="contractId" value="${contract.id}">
                        <input type="hidden" name="enterpriseId" value="${contract.enterpriseId}">
                    </c:if>

                    <div class="form-group">
                        <label>Doanh nghiệp</label>
                        <c:if test="${not empty technicalRequest}">
                            <div class="form-control-static">${technicalRequest.enterpriseName}</div>
                        </c:if>
                        <c:if test="${not empty contract}">
                            <div class="form-control-static">${contract.enterpriseName}</div>
                        </c:if>
                    </div>

                    <div class="form-group star-rating-group">
                        <label>Mức độ hài lòng của bạn?</label>
                        <div class="star-rating" id="star-container">
                            <i class="star" data-value="1" data-feather="star"></i>
                            <i class="star" data-value="2" data-feather="star"></i>
                            <i class="star" data-value="3" data-feather="star"></i>
                            <i class="star" data-value="4" data-feather="star"></i>
                            <i class="star" data-value="5" data-feather="star"></i>
                        </div>
                        <p id="rating-caption">Chọn một đánh giá</p>
                    </div>

                    <div class="form-group">
                        <label for="comments">Bạn có muốn chia sẻ thêm điều gì không?</label>
                        <textarea id="comments" name="comment" rows="5" placeholder="Hãy cho chúng tôi biết điều gì đã làm bạn hài lòng hoặc chúng tôi có thể cải thiện ở điểm nào..."></textarea>
                    </div>

                    <div class="form-group">
                        <button type="submit" class="btn-submit">Gửi phản hồi</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', () => {
                feather.replace({
                    width: '32px', height: '32px', 'stroke-width': 1.5
                });

                const stars = document.querySelectorAll('.star-rating .star');
                const ratingValueInput = document.getElementById('rating-value');
                const ratingCaption = document.getElementById('rating-caption');
                let currentRating = 0;

                const ratingCaptions = {
                    0: "Chọn một đánh giá", 1: "Rất tệ", 2: "Không hài lòng", 3: "Bình thường", 4: "Hài lòng", 5: "Tuyệt vời!"
                };

                const updateStars = (rating) => {
                    stars.forEach(star => {
                        const isSelected = star.dataset.value <= rating;
                        star.classList.toggle('selected', isSelected);
                        star.style.fill = isSelected ? '#facc15' : 'none';
                    });
                    ratingCaption.textContent = ratingCaptions[rating];
                };

                stars.forEach(star => {
                    star.addEventListener('mouseover', () => {
                        const rating = star.dataset.value;
                        stars.forEach(s => {
                            s.style.fill = s.dataset.value <= rating ? '#fde047' : 'none';
                        });
                    });

                    star.addEventListener('mouseout', () => {
                        updateStars(currentRating);
                    });

                    star.addEventListener('click', () => {
                        currentRating = star.dataset.value;
                        if (ratingValueInput) {
                            ratingValueInput.value = currentRating;
                        }
                        updateStars(currentRating);
                    });
                });
            });
        </script>
    </body>
</html>