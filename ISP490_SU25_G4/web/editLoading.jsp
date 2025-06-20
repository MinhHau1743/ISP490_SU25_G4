<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Đang cập nhật...</title>
    <meta http-equiv="refresh" content="5;url=${redirectUrl}"> <%-- Tăng thời gian chờ lên một chút --%>
    <script>
        // Chuyển hướng bằng JavaScript để đảm bảo hoạt động tốt hơn
        setTimeout(function() {
            window.location.href = "${redirectUrl}";
        }, 5000); // 5 giây
    </script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500&display=swap');

        body {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            background-color: #f0f2f5;
            font-family: 'Be Vietnam Pro', sans-serif;
            overflow: hidden;
        }

        .container {
            text-align: center;
            padding: 40px;
            border-radius: 15px;
            background-color: #ffffff;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
        }

        .loader {
            margin: 0 auto 25px;
            border: 8px solid #e3e3e3;
            border-top: 8px solid #5c67f2;
            border-radius: 50%;
            width: 70px;
            height: 70px;
            animation: spin 1.5s cubic-bezier(0.68, -0.55, 0.27, 1.55) infinite;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        .loading-text {
            color: #333;
            font-size: 24px;
            font-weight: 500;
        }

        .sub-text {
            color: #666;
            font-size: 16px;
            margin-top: 10px;
        }

        /* Hiệu ứng cho các dấu chấm */
        .loading-text .dots span {
            animation: blink 1.4s infinite both;
        }
        .loading-text .dots span:nth-child(2) {
            animation-delay: 0.2s;
        }
        .loading-text .dots span:nth-child(3) {
            animation-delay: 0.4s;
        }

        @keyframes blink {
            0% { opacity: .2; }
            20% { opacity: 1; }
            100% { opacity: .2; }
        }

    </style>
</head>
<body>
    <div class="container">
        <div class="loader"></div>
        <div class="loading-text">
            Đang cập nhật sản phẩm<span class="dots"><span>.</span><span>.</span><span>.</span></span>
        </div>
        <div class="sub-text">Vui lòng chờ trong giây lát! Hệ thống sẽ tự động chuyển hướng.</div>
    </div>
</body>
</html>