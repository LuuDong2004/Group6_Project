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
            .then(res => {
                if (!res.ok) throw new Error('Not found');
                return res.json();
            })
            .then(data => {
                // Đảm bảo các phần tử tồn tại trước khi gán
                const img = document.getElementById('personImage');
                const name = document.getElementById('personName');
                const desc = document.getElementById('personDescription');
                const role = document.getElementById('personRole');
                if (img) img.src = data.image ? ('assets/images/' + data.image) : 'assets/images/default-avatar.png';
                if (name) name.textContent = data.name || '';
                if (desc) desc.textContent = data.biography || data.description || '';
                if (role) role.textContent = type === 'actor' ? 'Diễn viên' : 'Đạo diễn';
            })
            .catch(() => {
                document.body.innerHTML = '<h2>Không tìm thấy thông tin</h2>';
            });
    } else {
        document.body.innerHTML = '<h2>Không tìm thấy thông tin</h2>';
    }
} 