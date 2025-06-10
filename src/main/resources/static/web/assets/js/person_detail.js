// person_detail.js
function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

const type = getQueryParam('type');
const id = getQueryParam('id');

if (!type || !id) {
    document.body.innerHTML = '<h2>Không tìm thấy thông tin</h2>';
} else {
    let apiUrl = '';
    if (type === 'actor') apiUrl = `/api/actors/${id}`;
    else if (type === 'director') apiUrl = `/api/directors/${id}`;
    else apiUrl = '';

    if (apiUrl) {
        fetch(apiUrl)
            .then(res => res.json())
            .then(data => {
                document.getElementById('personImage').src = data.image ? ('assets/images/' + data.image) : 'assets/images/default-avatar.png';
                document.getElementById('personName').textContent = data.name;
                document.getElementById('personDescription').textContent = data.description || '';
                document.getElementById('personRole').textContent = type === 'actor' ? 'Diễn viên' : 'Đạo diễn';
                // Có thể bổ sung API lấy danh sách phim đã tham gia nếu cần
            })
            .catch(() => {
                document.body.innerHTML = '<h2>Không tìm thấy thông tin</h2>';
            });
    } else {
        document.body.innerHTML = '<h2>Không tìm thấy thông tin</h2>';
    }
} 