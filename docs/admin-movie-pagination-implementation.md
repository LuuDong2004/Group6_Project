# Triển khai Phân trang cho Danh sách Phim Admin

## Tổng quan
Tài liệu này mô tả việc triển khai chức năng phân trang cho danh sách phim trong giao diện quản trị admin. Mỗi trang hiển thị 10 phim và có thể điều hướng giữa các trang.

## Yêu cầu
- Hiển thị 10 phim mỗi trang
- Có thể chuyển trang khi vượt quá số lượng
- Hỗ trợ tìm kiếm và lọc với phân trang
- Giao diện pagination thân thiện với người dùng

## Kiến trúc Implementation

### 1. Service Layer (IAdminMovieService & AdminMoiveServiceImpl)

#### Thêm phương thức mới:
```java
// Phân trang cho hiển thị tất cả phim
Page<MovieDto> getAllMoviesForDisplayWithPagination(Pageable pageable);

// Phân trang cho tìm kiếm và lọc phim
Page<MovieDto> getFilteredMoviesForDisplayWithPagination(String searchTerm, String filterBy, Pageable pageable);
```

#### Ưu điểm:
- Sử dụng Spring Data JPA Pageable interface
- Tự động xử lý việc đếm tổng số records
- Hỗ trợ sorting và pagination cùng lúc

### 2. Repository Layer (AdminMovieRepository)

#### Thêm các query methods với pagination:
```java
@Query(value = "SELECT DISTINCT m FROM Movie m " +
        "LEFT JOIN FETCH m.directors " +
        "LEFT JOIN FETCH m.actors " +
        "ORDER BY m.name",
        countQuery = "SELECT COUNT(DISTINCT m) FROM Movie m")
Page<Movie> findAllWithDirectorsAndActorsPageable(Pageable pageable);
```

#### Đặc điểm quan trọng:
- Sử dụng `countQuery` riêng biệt để tối ưu performance
- `LEFT JOIN FETCH` để eager loading directors và actors
- Hỗ trợ tất cả các filter criteria (name, description, genre, rating, language, year, director, actor)

### 3. Controller Layer (AdminMovieController)

#### Cập nhật method listMovies():
```java
@GetMapping("/list")
public String listMovies(Model model,
        @RequestParam(value = "searchTerm", required = false) String searchTerm,
        @RequestParam(value = "filterBy", required = false, defaultValue = "name") String filterBy,
        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        @RequestParam(value = "size", required = false, defaultValue = "10") int size)
```

#### Thêm parameters:
- `page`: Số trang hiện tại (bắt đầu từ 0)
- `size`: Số lượng items mỗi trang (mặc định 10)

#### Model attributes được thêm:
- `moviePage`: Page object chứa thông tin pagination
- `currentPage`: Trang hiện tại
- `totalPages`: Tổng số trang
- `totalElements`: Tổng số phim
- `startPage`, `endPage`: Để hiển thị pagination controls

### 4. View Layer (admin_movie_list.html)

#### Cập nhật pagination controls:
```html
<div class="pagination-controls">
    <!-- Nút Previous -->
    <a th:if="${currentPage > 0}" 
       th:href="@{/admin/movies/list(page=${currentPage - 1}, size=${size}, searchTerm=${searchTerm}, filterBy=${filterBy})}"
       class="pagination-btn">Trước</a>
    
    <!-- Các số trang -->
    <th:block th:each="pageNum : ${#numbers.sequence(startPage, endPage)}">
        <a th:if="${pageNum != currentPage}"
           th:href="@{/admin/movies/list(page=${pageNum}, size=${size}, searchTerm=${searchTerm}, filterBy=${filterBy})}"
           th:text="${pageNum + 1}"
           class="pagination-btn">1</a>
        <button th:if="${pageNum == currentPage}"
                th:text="${pageNum + 1}"
                class="pagination-btn active">1</button>
    </th:block>
    
    <!-- Nút Next -->
    <a th:if="${currentPage < totalPages - 1}"
       th:href="@{/admin/movies/list(page=${currentPage + 1}, size=${size}, searchTerm=${searchTerm}, filterBy=${filterBy})}"
       class="pagination-btn">Tiếp</a>
</div>
```

## Tính năng chính

### 1. Phân trang cơ bản
- Hiển thị 10 phim mỗi trang
- Điều hướng Previous/Next
- Hiển thị số trang hiện tại và tổng số trang

### 2. Tích hợp với tìm kiếm
- Giữ nguyên search term và filter khi chuyển trang
- Pagination reset về trang đầu khi thay đổi search criteria

### 3. Thông tin hiển thị
- Hiển thị số lượng kết quả tìm thấy
- Hiển thị vị trí hiện tại (ví dụ: "Hiển thị 1 đến 10 của 248 phim")
- Hiển thị trang hiện tại và tổng số trang

### 4. Performance optimization
- Sử dụng countQuery riêng biệt
- Eager loading chỉ cho dữ liệu cần thiết
- Pagination ở database level

## Cách sử dụng

### URL Examples:
- Trang đầu: `/admin/movies/list`
- Trang 2: `/admin/movies/list?page=1`
- Tìm kiếm với pagination: `/admin/movies/list?searchTerm=action&page=0&size=10`
- Lọc theo genre: `/admin/movies/list?searchTerm=comedy&filterBy=genre&page=1`

### Parameters:
- `page`: Số trang (0-based index)
- `size`: Số items mỗi trang (mặc định 10)
- `searchTerm`: Từ khóa tìm kiếm
- `filterBy`: Tiêu chí lọc (name, description, genre, rating, language, releaseyear, director, actor)

## Lợi ích

### 1. Performance
- Giảm tải database bằng cách chỉ load dữ liệu cần thiết
- Giảm thời gian response time
- Tiết kiệm bandwidth

### 2. User Experience
- Tải trang nhanh hơn
- Dễ dàng điều hướng qua nhiều trang
- Thông tin rõ ràng về vị trí hiện tại

### 3. Scalability
- Có thể xử lý hàng nghìn phim mà không ảnh hưởng performance
- Dễ dàng thay đổi số lượng items mỗi trang

## Kết luận

Implementation này cung cấp một giải pháp pagination hoàn chỉnh cho danh sách phim admin với:
- Performance tối ưu
- User experience tốt
- Tích hợp seamless với tính năng tìm kiếm và lọc
- Code maintainable và extensible

Chức năng này giúp admin dễ dàng quản lý danh sách phim lớn một cách hiệu quả và thuận tiện.
