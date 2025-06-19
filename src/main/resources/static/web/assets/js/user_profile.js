function getUserIdFromQuery() {
    const params = new URLSearchParams(window.location.search);
    return params.get('id');
}
const userId = getUserIdFromQuery();
if (userId) {
    fetch(`/api/users/${userId}`)
        .then(res => res.json())
        .then(user => {
            document.getElementById('user-name').textContent = user.userName;
            document.getElementById('email').textContent = user.email;
            document.getElementById('date-of-birth').textContent = user.dateOfBirth;
            document.getElementById('phone').textContent = user.phone;
            document.getElementById('address').textContent = user.address;
        })
        .catch(() => {
            document.querySelector('.profile-info').innerHTML = '<p>Không tìm thấy người dùng.</p>';
        });
} else {
    document.querySelector('.profile-info').innerHTML = '<p>Không có ID người dùng.</p>';
} 