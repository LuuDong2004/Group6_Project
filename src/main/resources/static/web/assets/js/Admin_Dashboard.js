
    document.addEventListener('DOMContentLoaded', function() {
        initializeDashboardBehavior();
        initializeGenresForForm(); // Đổi tên hàm để rõ ràng hơn
        initializeFileUpload();
        initializeFormValidationAndSubmission(); // Đổi tên hàm
        initializeMovieListViewInteractions(); // Đổi tên hàm
        initializeViewSwitcher();
    });

    function initializeViewSwitcher() {
        const movieListView = document.getElementById('movieListView');
        const addMovieFormView = document.getElementById('addMovieFormView');
        const addMovieBtn = document.querySelector('.add-movie-btn'); // Nút "Thêm Phim Mới" trong danh sách
        const cancelBtn = document.getElementById('cancelBtn'); // Nút "Hủy" trong form
        const headerTitle = document.getElementById('main-header-title');

        addMovieBtn.addEventListener('click', function() {
            movieListView.classList.add('d-none');
            addMovieFormView.classList.remove('d-none');
            headerTitle.textContent = 'Thêm Phim Mới';
        });

        cancelBtn.addEventListener('click', function() {
            addMovieFormView.classList.add('d-none');
            movieListView.classList.remove('d-none');
            headerTitle.textContent = 'Quản lý Phim';
             // Reset form khi hủy
            document.getElementById('movieForm').reset();
            document.getElementById('movieForm').classList.remove('was-validated');
            if(document.getElementById('imagePreview')) {
                document.getElementById('imagePreview').classList.add('d-none');
                 if(document.getElementById('removeImageBtn')) {
                    document.getElementById('removeImageBtn').classList.add('d-none');
                }
            }
             document.getElementById('posterFile').value = '';
            document.querySelectorAll('#genreContainerForm .genre-tag-form.selected').forEach(tag => tag.classList.remove('selected'));
        });
    }

    function initializeMovieListViewInteractions() {
        const editBtns = document.querySelectorAll('#movieListView .edit-btn');
        editBtns.forEach(btn => {
            btn.addEventListener('click', function() {
                // Logic sửa phim: có thể lấy ID phim từ data-attribute và mở form với dữ liệu đó
                alert('Chức năng sửa phim sẽ được thực hiện ở đây.');
                // Ví dụ: chuyển sang form thêm/sửa với dữ liệu phim cần sửa
                // document.getElementById('movieListView').classList.add('d-none');
                // document.getElementById('addMovieFormView').classList.remove('d-none');
                // document.getElementById('main-header-title').textContent = 'Sửa Thông Tin Phim';
                // populateEditForm(movieId); // Hàm này bạn sẽ cần tự viết
            });
        });

        const deleteBtns = document.querySelectorAll('#movieListView .delete-btn');
        deleteBtns.forEach(btn => {
            btn.addEventListener('click', function() {
                // Sử dụng modal Bootstrap thay cho confirm
                // const confirmModal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
                // Cần thêm modal HTML vào trang của bạn
                // document.getElementById('confirmDeleteButton').onclick = function() {
                //     alert('Chức năng xóa phim sẽ được thực hiện ở đây.');
                //     confirmModal.hide();
                //     // Xóa hàng khỏi bảng hoặc tải lại dữ liệu
                // };
                // confirmModal.show();
                if (confirm('Bạn có chắc chắn muốn xóa phim này không?')) {
                     alert('Chức năng xóa phim sẽ được thực hiện ở đây.');
                    // Xóa hàng khỏi bảng hoặc tải lại dữ liệu
                }
            });
        });

        const paginationBtns = document.querySelectorAll('#movieListView .pagination-btn');
        paginationBtns.forEach(btn => {
            btn.addEventListener('click', function() {
                if (!this.classList.contains('active') && (this.textContent !== 'Trước' && this.textContent !== 'Tiếp')) {
                    paginationBtns.forEach(b => b.classList.remove('active'));
                    this.classList.add('active');
                    // Logic tải dữ liệu trang mới
                }
                // Logic cho nút Trước/Tiếp
            });
        });
    }

    function initializeDashboardBehavior() {
        const sidebarToggle = document.getElementById('sidebarToggle');
        const sidebar = document.getElementById('sidebar');
        const mainContentWrapper = document.getElementById('mainContentWrapper');
        const headerCustom = document.querySelector('.header-custom');


        function toggleSidebar() {
            sidebar.classList.toggle('collapsed');
            mainContentWrapper.classList.toggle('expanded');
            if (headerCustom) { // Kiểm tra headerCustom có tồn tại không
                 // Không cần thay đổi left của header nữa vì nó được xử lý bằng margin-left của mainContentWrapper
            }
        }

        sidebarToggle.addEventListener('click', toggleSidebar);

        function checkViewport() {
            if (window.innerWidth <= 992) { // Thay đổi breakpoint nếu cần
                if (!sidebar.classList.contains('collapsed')) {
                    toggleSidebar();
                }
            } else {
                if (sidebar.classList.contains('collapsed')) {
                     // Mở lại sidebar nếu màn hình đủ lớn và sidebar đang đóng (tùy chọn)
                     // toggleSidebar();
                }
            }
        }
        checkViewport(); // Kiểm tra khi tải trang
        window.addEventListener('resize', checkViewport);
    }

    function initializeGenresForForm() {
        const genres = [
            'Hành động', 'Phiêu lưu', 'Hài', 'Chính kịch', 'Giả tưởng', 'Kinh dị',
            'Bí ẩn', 'Lãng mạn', 'Viễn tưởng', 'Giật gân', 'Hoạt hình', 'Tài liệu'
        ];
        const genreContainerForm = document.getElementById('genreContainerForm'); // Sử dụng ID mới
        if (!genreContainerForm) return; // Thoát nếu không tìm thấy container

        let selectedGenres = [];

        genres.forEach(genre => {
            const genreTag = document.createElement('span');
            genreTag.className = 'genre-tag-form'; // Sử dụng class mới
            genreTag.textContent = genre;
            genreTag.dataset.genre = genre; // Lưu trữ giá trị genre
            genreTag.addEventListener('click', function() {
                this.classList.toggle('selected');
                const genreValue = this.dataset.genre;
                if (this.classList.contains('selected')) {
                    if (!selectedGenres.includes(genreValue)) {
                        selectedGenres.push(genreValue);
                    }
                } else {
                    selectedGenres = selectedGenres.filter(g => g !== genreValue);
                }
                // Thêm hiệu ứng nhỏ khi click
                this.style.transform = 'scale(0.97)';
                setTimeout(() => {
                    this.style.transform = 'scale(1)';
                }, 150);
            });
            genreContainerForm.appendChild(genreTag);
        });
    }

    function initializeFileUpload() {
        const uploadArea = document.getElementById('uploadArea');
        const fileInput = document.getElementById('posterFile');
        const imagePreview = document.getElementById('imagePreview');
        const previewImg = document.getElementById('previewImg');
        const removeImageBtn = document.getElementById('removeImageBtn');

        if (!uploadArea || !fileInput || !imagePreview || !previewImg || !removeImageBtn) return;

        uploadArea.addEventListener('click', () => fileInput.click());
        uploadArea.addEventListener('dragover', (e) => { e.preventDefault(); uploadArea.classList.add('dragover'); });
        uploadArea.addEventListener('dragleave', (e) => { e.preventDefault(); uploadArea.classList.remove('dragover'); });
        uploadArea.addEventListener('drop', (e) => {
            e.preventDefault();
            uploadArea.classList.remove('dragover');
            const files = e.dataTransfer.files;
            if (files.length > 0) handleFileSelect(files[0]);
        });
        fileInput.addEventListener('change', (e) => {
            if (e.target.files.length > 0) handleFileSelect(e.target.files[0]);
        });

        function handleFileSelect(file) {
            if (file.type.startsWith('image/')) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    previewImg.src = e.target.result;
                    imagePreview.classList.remove('d-none');
                    removeImageBtn.classList.remove('d-none'); // Show remove button
                    imagePreview.style.opacity = '0';
                    setTimeout(() => {
                        imagePreview.style.transition = 'opacity 0.3s ease';
                        imagePreview.style.opacity = '1';
                    }, 50);
                };
                reader.readAsDataURL(file);
            } else {
                alert("Vui lòng chọn một tệp hình ảnh hợp lệ (PNG, JPG).");
                previewImg.src = '';
                imagePreview.classList.add('d-none');
                removeImageBtn.classList.add('d-none');
                fileInput.value = ''; // Reset file input
            }
        }

        removeImageBtn.addEventListener('click', function() {
            previewImg.src = '';
            imagePreview.classList.add('d-none');
            removeImageBtn.classList.add('d-none');
            fileInput.value = ''; // Important: Reset file input to allow re-selecting the same file
        });
    }

    function initializeFormValidationAndSubmission() {
        const form = document.getElementById('movieForm');
        const submitBtn = document.getElementById('submitBtn');
        const successMessage = document.getElementById('successMessage');

        if (!form || !submitBtn || !successMessage) return; // Thoát nếu thiếu element

        form.addEventListener('submit', function(e) {
            e.preventDefault();

            // Validate form (Bootstrap 5 validation)
            if (!form.checkValidity()) {
                e.stopPropagation();
                form.classList.add('was-validated');
                return;
            }
            form.classList.add('was-validated');


            const originalText = submitBtn.innerHTML;
            submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Đang xử lý...';
            submitBtn.disabled = true;

            // Lấy dữ liệu form (ví dụ)
            const formData = {
                title: document.getElementById('movieTitle').value,
                description: document.getElementById('description').value,
                // ... lấy các trường khác
                genres: Array.from(document.querySelectorAll('#genreContainerForm .genre-tag-form.selected')).map(tag => tag.dataset.genre)
            };
            console.log("Dữ liệu phim:", formData);


            setTimeout(() => {
                submitBtn.innerHTML = originalText;
                submitBtn.disabled = false;
                successMessage.textContent = "Thêm phim thành công!";
                successMessage.classList.remove('d-none');
                window.scrollTo({ top: 0, behavior: 'smooth' });

                setTimeout(() => {
                    successMessage.classList.add('d-none');
                }, 5000);

                form.reset();
                form.classList.remove('was-validated');
                if(document.getElementById('imagePreview')) {
                    document.getElementById('imagePreview').classList.add('d-none');
                    if(document.getElementById('removeImageBtn')) { // Hide remove button on reset
                        document.getElementById('removeImageBtn').classList.add('d-none');
                    }
                }
                document.getElementById('posterFile').value = ''; // Reset file input on form reset
                document.querySelectorAll('#genreContainerForm .genre-tag-form.selected').forEach(tag => tag.classList.remove('selected'));

                // Chuyển về danh sách phim
                document.getElementById('addMovieFormView').classList.add('d-none');
                document.getElementById('movieListView').classList.remove('d-none');
                document.getElementById('main-header-title').textContent = 'Quản lý Phim';
            }, 1500);
        });
    }
