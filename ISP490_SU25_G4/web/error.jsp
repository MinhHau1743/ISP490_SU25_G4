<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
    <head>
        <title>404 - Not Found</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                text-align: center;
                padding: 50px;
                background: #f4f4f4;
            }
            .error-box {
                background: white;
                padding: 30px;
                border-radius: 5px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }
            h1 {
                color: #d32f2f;
                font-size: 48px;
            }
            a {
                color: #1976d2;
                text-decoration: none;
            }
        </style>
    </head>
    <body>
        <div class="error-box">
            <h1>404</h1>
            <h2>Trang không tồn tại</h2>
            <p>Trang bạn đang tìm kiếm không thể được tìm thấy.</p>
            <p><a href="#" onclick="goBack()">← Quay lại trang trước</a></p>

            <script>
                function goBack() {
                    window.history.back();
                }
            </script>
        </div>
    </body>
</html>