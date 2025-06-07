
            document.addEventListener("DOMContentLoaded", function () {
                const alertBox = document.getElementById("customAlert");
                const progressBar = document.getElementById("alertProgressBar");
                if (alertBox && progressBar) {
                    setTimeout(() => {
                        progressBar.style.width = '0%';
                    }, 100);
                    setTimeout(() => {
                        $(alertBox).alert('close');
                    }, 5100);
                }
            });
// Hide loading overlay when page fully loaded
            window.addEventListener("load", function () {
                const overlay = document.getElementById("loadingOverlay");
                if (overlay) {
                    overlay.style.opacity = "0";
                    overlay.style.transition = "opacity 0.5s ease";
                    setTimeout(() => overlay.style.display = "none", 500);
                }
            });
