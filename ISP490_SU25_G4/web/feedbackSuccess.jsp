<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Cảm ơn bạn! - DPCRM</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <script src="https://unpkg.com/feather-icons"></script>
        <link rel="icon" href="${pageContext.request.contextPath}/image/logo.png" type="image/png">
        <style>
            html, body {
                height: 100%;
                font-family: 'Inter', sans-serif;
                margin: 0;
                background-color: #f9fafb;
                display: flex;
                justify-content: center;
                align-items: center;
            }
            .success-container {
                max-width: 500px;
                width: 100%;
                background-color: white;
                padding: 40px;
                border-radius: 16px;
                border: 1px solid #e5e7eb;
                box-shadow: 0 10px 15px -3px rgb(0 0 0 / 0.07), 0 4px 6px -4px rgb(0 0 0 / 0.1);
                text-align: center;
            }
            .icon-wrapper {
                width: 64px;
                height: 64px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                background-color: #dcfce7; /* green-100 */
                color: #16a34a; /* green-600 */
                border-radius: 50%;
                margin-bottom: 24px;
            }
            h1 {
                font-size: 24px;
                font-weight: 700;
                color: #111827;
                margin: 0 0 12px 0;
            }
            p {
                font-size: 16px;
                color: #6b7280;
                margin: 0;
                line-height: 1.6;
            }
        </style>
    </head>
    <body>
        <div class="success-container">
            <div class="icon-wrapper">
                <i data-feather="check-circle" style="width: 36px; height: 36px;"></i>
            </div>
            <h1>Gửi phản hồi thành công!</h1>
            <p>
                Cảm ơn bạn đã dành thời gian chia sẻ ý kiến. 
                Chúng tôi rất trân trọng những đóng góp của bạn để cải thiện dịch vụ tốt hơn.
            </p>
            <p style="margin-top: 16px; font-size: 14px;">
                Bạn có thể đóng trang này.
            </p>
        </div>

        <script>
            feather.replace();
        </script>
    </body>
</html>