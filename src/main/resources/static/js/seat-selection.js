let selectedSeats = [];
const maxSeats = 8;

function initializeSeatSelection(scheduleId, userId) {
    this.scheduleId = scheduleId;
    this.userId = userId;

    // Initialize seat map
    const seats = document.querySelectorAll('.seat');
    seats.forEach(seat => {
        const status = seat.getAttribute('data-status');
        if (status === 'RESERVED') {
            seat.classList.add('reserved');
            seat.classList.remove('available');
        }
    });
}

function renderSeats() {
    const seatContainer = document.getElementById('seat-container');
    if (!seatContainer) return;

    seatContainer.innerHTML = '';

    // Tạo 8 hàng ghế (A-H)
    for (let row = 0; row < 8; row++) {
        const rowElement = document.createElement('div');
        rowElement.className = 'seat-row';
        
        // Tạo 8 ghế cho mỗi hàng
        for (let col = 0; col < 8; col++) {
            const seatNumber = col + 1;
            const seatName = `${String.fromCharCode(65 + row)}${seatNumber}`;
            const seatId = row * 8 + seatNumber;
            
            const seatElement = document.createElement('div');
            seatElement.className = 'seat';
            seatElement.textContent = seatName;
            seatElement.dataset.seatId = seatId;
            seatElement.dataset.seatName = seatName;

            // Kiểm tra trạng thái ghế
            const isReserved = this.reservedSeats.some(seat => 
                seat.seatId === seatId && seat.status === 'RESERVED');
            const isPending = this.reservedSeats.some(seat => 
                seat.seatId === seatId && seat.status === 'PENDING');
            
            if (isReserved) {
                seatElement.classList.add('reserved');
            } else if (isPending) {
                seatElement.classList.add('pending');
            } else {
                seatElement.classList.add('available');
                seatElement.addEventListener('click', () => this.toggleSeat(seatId, seatName, seatElement));
            }

            rowElement.appendChild(seatElement);
        }
        
        seatContainer.appendChild(rowElement);
    }
}

function toggleSeat(seatId, seatName, seatElement) {
    console.log('Toggling seat:', seatId, seatName); // Debug log

    // Kiểm tra nếu ghế đã được đặt
    if (seatElement.classList.contains('reserved')) {
        alert('Ghế này đã được đặt');
        return;
    }

    // Kiểm tra nếu ghế đang trong trạng thái pending
    if (seatElement.classList.contains('pending')) {
        alert('Ghế này đang được xử lý, vui lòng đợi trong giây lát');
        return;
    }

    if (seatElement.classList.contains('selected')) {
        // Deselect seat
        seatElement.classList.remove('selected');
        seatElement.classList.add('available');
        selectedSeats = selectedSeats.filter(seat => seat.id !== parseInt(seatId));
    } else {
        // Select seat (limit to 8 seats)
        if (selectedSeats.length < maxSeats) {
            seatElement.classList.remove('available');
            seatElement.classList.add('selected');
            selectedSeats.push({
                id: parseInt(seatId),
                name: seatName
            });
            updateSelectedSeatsInfo();
        } else {
            alert('Bạn chỉ có thể chọn tối đa 8 ghế');
        }
    }
}

function updateSelectedSeatsInfo() {
    const selectedSeatsList = document.getElementById('selectedSeatsList');
    selectedSeatsList.innerHTML = selectedSeats.map(seat => 
        `<div class="selected-seat">
            <span>${seat.name}</span>
            <button onclick="toggleSeat(${seat.id}, '${seat.name}', this.parentElement)">
                <i class="fas fa-times"></i>
            </button>
        </div>`
    ).join('');

    // Update total price
    const totalPrice = selectedSeats.length * 75000; // Giá vé 75,000đ
    document.getElementById('totalPrice').textContent = totalPrice.toLocaleString('vi-VN') + 'đ';

    // Enable/disable continue button
    const continueButton = document.getElementById('continueButton');
    continueButton.disabled = selectedSeats.length === 0;
}

function proceedToPayment() {
    if (selectedSeats.length === 0) {
        alert('Vui lòng chọn ít nhất một ghế');
        return;
    }

    // Đánh dấu các ghế đã chọn là pending
    selectedSeats.forEach(seat => {
        const seatElement = document.querySelector(`[data-seat-id="${seat.id}"]`);
        if (seatElement) {
            seatElement.classList.remove('selected');
            seatElement.classList.add('pending');
        }
    });

    // Chuyển hướng đến trang thanh toán với dữ liệu đã chọn
    const seatIds = selectedSeats.map(seat => seat.id).join(',');
    const seatNames = selectedSeats.map(seat => seat.name).join(',');
    const total = selectedSeats.length * 75000;

    window.location.href = `/payment?scheduleId=${this.scheduleId}&seatIds=${seatIds}&seatNames=${seatNames}&total=${total}`;
}

// Export functions
window.seatSelection = {
    initialize: initializeSeatSelection,
    toggleSeat: toggleSeat,
    updateSelectedSeatsInfo: updateSelectedSeatsInfo,
    proceedToPayment: proceedToPayment
}; 