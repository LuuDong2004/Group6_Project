package group6.cinema_project.controller.User;

import group6.cinema_project.entity.Qa.Review;
import group6.cinema_project.entity.Movie;
import group6.cinema_project.repository.User.ReviewRepository;
import group6.cinema_project.repository.User.UserRepository;
import group6.cinema_project.repository.User.MovieRepository;
import group6.cinema_project.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addReview(@RequestParam Integer movieId,
                                       @RequestParam String comment,
                                       @RequestParam Integer rating) {
        try {
            // Lấy user hiện tại
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Bạn cần đăng nhập để viết đánh giá");
            }

            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với email: " + email));

            // Kiểm tra xem user đã đánh giá phim này chưa
            Review existingReview = reviewRepository.findByMovieIdAndUserId(movieId.longValue(), Long.valueOf(user.getId()));
            if (existingReview != null) {
                return ResponseEntity.badRequest().body("Bạn đã đánh giá phim này rồi");
            }

            // Tạo review mới
            Review review = new Review();
            // Lấy movie từ database
            Movie movie = movieRepository.findById(movieId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phim với ID: " + movieId));

            review.setMovie(movie);
            review.setUserId(Long.valueOf(user.getId()));
            review.setComment(comment);
            review.setRating(rating.intValue());
            review.setDate(LocalDateTime.now());

            // Lưu review
            Review savedReview = reviewRepository.save(review);

            // Trả về thông tin review đã lưu
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đánh giá đã được gửi thành công!");

            Map<String, Object> reviewData = new HashMap<>();
            reviewData.put("id", savedReview.getId());
            reviewData.put("user", "User " + user.getId());
            reviewData.put("comment", savedReview.getComment());
            reviewData.put("rating", Integer.valueOf(savedReview.getRating()));
            reviewData.put("date", savedReview.getDate());
            response.put("review", reviewData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi gửi đánh giá: " + e.getMessage());
        }
    }

    @GetMapping("/movie/{movieId}")
    @ResponseBody
    public ResponseEntity<?> getReviewsByMovieId(@PathVariable Integer movieId) {
        try {
            var reviews = reviewRepository.findByMovie_Id(movieId.longValue());
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi lấy đánh giá: " + e.getMessage());
        }
    }
}