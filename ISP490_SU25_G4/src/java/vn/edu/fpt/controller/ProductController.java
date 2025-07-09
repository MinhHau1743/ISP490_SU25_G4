package vn.edu.fpt.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vn.edu.fpt.dao.ProductDAO;
import vn.edu.fpt.dao.ProductCategoriesDAO;
import vn.edu.fpt.model.Product;
import vn.edu.fpt.model.ProductFilterCacheKey;
import vn.edu.fpt.model.ProductCategory;

@WebServlet(name = "ProductController", urlPatterns = {"/ProductController"})
@MultipartConfig
public class ProductController extends HttpServlet {

    // ====== KHAI BÁO CACHE (giới hạn tối đa 1000 entry, FIFO) ======
    private static final int MAX_CACHE_SIZE = 1000;

    // Cache cho danh sách sản phẩm
    private static final Map<ProductFilterCacheKey, List<Product>> productCache =
        new LinkedHashMap<ProductFilterCacheKey, List<Product>>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<ProductFilterCacheKey, List<Product>> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };

    // Cache cho tổng số sản phẩm
    private static final Map<ProductFilterCacheKey, Integer> countCache =
        new LinkedHashMap<ProductFilterCacheKey, Integer>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<ProductFilterCacheKey, Integer> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String service = request.getParameter("service");
        ProductDAO products = new ProductDAO();
        ProductCategoriesDAO productCategories = new ProductCategoriesDAO();

        int page = 1;
        int pageSize = 10;

        String pageRaw = request.getParameter("page");
        String sizeRaw = request.getParameter("size");
        if (pageRaw != null) {
            try {
                page = Integer.parseInt(pageRaw);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        if (sizeRaw != null) {
            try {
                pageSize = Integer.parseInt(sizeRaw);
            } catch (NumberFormatException e) {
                pageSize = 10;
            }
        }

        // --- Lấy filter từ request ---
        String keyword = request.getParameter("keyword");
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        String origin = request.getParameter("origin");
        String categoryIdStr = request.getParameter("categoryId");

        Double minPrice = null, maxPrice = null;
        Integer categoryId = null;
        if (minPriceStr != null && !minPriceStr.isEmpty()) {
            try {
                minPrice = Double.parseDouble(minPriceStr);
            } catch (NumberFormatException e) {
                minPrice = null;
            }
        }
        if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
            try {
                maxPrice = Double.parseDouble(maxPriceStr);
            } catch (NumberFormatException e) {
                maxPrice = null;
            }
        }
        if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
            try {
                categoryId = Integer.parseInt(categoryIdStr);
            } catch (NumberFormatException e) {
                categoryId = null;
            }
        }
        if (origin != null && origin.trim().isEmpty()) {
            origin = null;
        }

        // --- Sử dụng cache ---
        ProductFilterCacheKey cacheKey = new ProductFilterCacheKey(keyword, minPrice, maxPrice, origin, categoryId, page, pageSize);
        List<Product> listProducts;
        Integer totalProducts;

        synchronized (productCache) {
            listProducts = productCache.get(cacheKey);
        }
        synchronized (countCache) {
            totalProducts = countCache.get(cacheKey);
        }

        if (listProducts == null || totalProducts == null) {
            totalProducts = products.countProductsWithFilter(keyword, minPrice, maxPrice, origin, categoryId);
            listProducts = products.getProductsWithFilter(keyword, minPrice, maxPrice, origin, categoryId, page, pageSize);

            synchronized (productCache) {
                productCache.put(cacheKey, listProducts);
            }
            synchronized (countCache) {
                countCache.put(cacheKey, totalProducts);
            }
        }
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

        // --- Lấy toàn bộ danh mục, map categoryId -> name ---
        List<ProductCategory> categories = productCategories.getAllCategories();
        Map<Integer, String> categoryMap = new HashMap<>();
        for (ProductCategory c : categories) {
            categoryMap.put(c.getId(), c.getName());
        }
        List<String> origins = products.getAllOrigins();
        request.setAttribute("originList", origins);
        request.setAttribute("productList", listProducts);
        request.setAttribute("categoryMap", categoryMap);
        request.setAttribute("categories", categories);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);

        // Gửi các filter sang JSP để giữ trạng thái filter
        request.setAttribute("keyword", keyword);
        request.setAttribute("minPrice", minPriceStr);
        request.setAttribute("maxPrice", maxPriceStr);
        request.setAttribute("origin", origin);
        request.setAttribute("categoryId", categoryIdStr);

        String notification = (String) request.getAttribute("Notification");
        if (notification != null && !notification.isEmpty()) {
            request.setAttribute("Notification", notification);
        }

        request.getRequestDispatcher("jsp/technicalSupport/listProduct.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "ProductController with in-memory cache for product filter";
    }
}
